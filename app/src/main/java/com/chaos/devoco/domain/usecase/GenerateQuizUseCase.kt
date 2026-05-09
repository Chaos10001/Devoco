package com.chaos.devoco.domain.usecase

import com.chaos.devoco.data.remote.api.GeminiApiService
import com.chaos.devoco.data.repository.PdfRepository
import com.chaos.devoco.data.repository.QuestionRepository
import com.chaos.devoco.domain.model.ObjectiveQuestion
import com.chaos.devoco.domain.model.QuizQuestion
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val pdfRepository: PdfRepository,
    private val questionRepository: QuestionRepository
) {
     suspend operator fun invoke(documentId: String) :Result<List<QuizQuestion>> {
         return try {
             val document = pdfRepository.getDocumentById(documentId) ?: return Result.failure(
                 Exception("Document not found")
             )

             val prompt = buildString {
                 append("Based on the following document text, generate EXACTLY 20 multiple-choice quiz questions. ")
                 append("Each question must have exactly 4 options (A, B, C, D). ")
                 append("Respond ONLY with a valid JSON array. No text before or after the JSON array.\n")
                 append("Format:\n")
                 append("[\n")
                 append("  {\n")
                 append("    \"question\": \"What is...?\",\n")
                 append("    \"optionA\": \"Option A text\",\n")
                 append("    \"optionB\": \"Option B text\",\n")
                 append("    \"optionC\": \"Option C text\",\n")
                 append("    \"optionD\": \"Option D text\",\n")
                 append("    \"correctAnswer\": \"A\",\n")
                 append("    \"explanation\": \"Explanation why A is correct\"\n")
                 append("  }\n")
                 append("]\n\n")
                 append("Document text:\n${document.extractedText.take(10000)}")
             }

             val result = geminiApiService.generateContent(prompt)

             result.fold(
                 onSuccess = { jsonString ->
                     val questions = parseQuizQuestions(jsonString)
                     questionRepository.saveQuizQuestions(documentId, questions)
                     Result.success(questions)
                 },
                 onFailure = {Result.failure(it)}
             )
         } catch (e: Exception){
             Result.failure(e)
         }
     }

    private fun parseQuizQuestions(jsonString: String) : List<QuizQuestion> {
        val cleanJson = jsonString.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val jsonArray = Json.parseToJsonElement(cleanJson).jsonArray

        return jsonArray.mapIndexed { index, element ->
            val obj = element.jsonObject
            val options = listOf(
                obj["optionA"]?.jsonPrimitive?.content?.trim()
                    ?: throw IllegalArgumentException("Missing field: optionA"),
                obj["optionB"]?.jsonPrimitive?.content?.trim()
                    ?: throw IllegalArgumentException("Missing field: optionB"),
                obj["optionC"]?.jsonPrimitive?.content?.trim()
                    ?: throw IllegalArgumentException("Missing field: optionC"),
                obj["optionD"]?.jsonPrimitive?.content?.trim()
                    ?: throw IllegalArgumentException("Missing field: optionD"),
            )
            
            // Clean the correct answer letter from AI (handle "A", "A.", "a", etc.)
            val correctLetter = obj["correctAnswer"]?.jsonPrimitive?.content
                ?.trim()
                ?.removeSuffix(".")
                ?.trim()
                ?.uppercase()
                ?: throw IllegalArgumentException("Missing field: correctAnswer")

            val correctAnswerText = when (correctLetter) {
                "A" -> options[0]
                "B" -> options[1]
                "C" -> options[2]
                "D" -> options[3]
                else -> correctLetter // Fallback if AI provides the full text instead of a letter
            }

            QuizQuestion(
                id = UUID.randomUUID().toString(),
                question = obj["question"]?.jsonPrimitive?.content?.trim()
                    ?: throw IllegalArgumentException("Missing field: question"),
                options = options,
                correctAnswer = correctAnswerText,
                explanation = obj["explanation"]?.jsonPrimitive?.content?.trim()
                    ?: throw IllegalArgumentException("Missing field: explanation"),
                questionNumber = index + 1
            )
        }
    }
}