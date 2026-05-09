package com.chaos.devoco.domain.usecase

import com.chaos.devoco.data.remote.api.GeminiApiService
import com.chaos.devoco.data.repository.PdfRepository
import com.chaos.devoco.data.repository.QuestionRepository
import com.chaos.devoco.domain.model.TheoryQuestion
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import javax.inject.Inject

class GenerateTheoryQuestionsUseCase @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val pdfRepository: PdfRepository,
    private val questionRepository: QuestionRepository
){
    suspend operator fun invoke(documentId : String) : Result<List<TheoryQuestion>>{
        return try {
            val document = pdfRepository.getDocumentById(documentId) ?: return Result.failure(
                Exception("Document not found"))

            val prompt = buildString {
                append("Based on the following document text, generate 30 unique and challenging theory questions. ")
                append("For every question, provide a comprehensive and detailed explanation as the answer based strictly on the document content. ") // Added instruction for unique content
                append("Respond ONLY with a valid JSON array. No text before or after the JSON array.\n")
                append("Format:\n")
                append("[\n")
                append("  {\n")
                append("    \"question\": \"[Specific question generated from text]\",\n") // Made placeholders more generic
                append("    \"answer\": \"[Detailed explanation generated from text]\"\n")
                append("  }\n")
                append("]\n\n")
                append("Document text:\n${document.extractedText.take(10000)}")
            }
            val result = geminiApiService.generateContent(prompt)
            result.fold(
                onSuccess = { jsonString ->
                    val questions= parseTheoryQuestions(jsonString)
                    questionRepository.saveTheoryQuestions(documentId,questions)
                    Result.success(questions)
                },
                onFailure = {Result.failure(it)}
            )
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    private fun parseTheoryQuestions(jsonString: String) : List<TheoryQuestion>{
        val cleanJson= jsonString.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val jsonArray = Json.parseToJsonElement(cleanJson).jsonArray
        return jsonArray.mapIndexed { index, element ->
            val obj = element.jsonObject

            TheoryQuestion(
                id = UUID.randomUUID().toString(),
                question = obj["question"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing field"),
                answer = obj["answer"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing field"),
                questionNumber = index + 1
            )
        }
    }
}