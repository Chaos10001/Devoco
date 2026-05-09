package com.chaos.devoco.data.repository

import com.chaos.devoco.data.local.db.dao.PdfDocumentDao
import com.chaos.devoco.data.local.db.entity.ObjectiveQuestionEntity
import com.chaos.devoco.data.local.db.entity.QuizQuestionEntity
import com.chaos.devoco.data.local.db.entity.TheoryQuestionEntity
import com.chaos.devoco.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val pdfDocumentDao: PdfDocumentDao
){

    // ==================== Objective Questions ====================
    suspend fun saveObjectiveQuestions(
        documentId: String,
        questions: List <ObjectiveQuestion>
    ){
        val entities = questions.map { question ->
            ObjectiveQuestionEntity(
                id = question.id,
                documentId= documentId,
                question= question.question,
                optionA = question.options.getOrElse(0) {""},
                optionB = question.options.getOrElse(1) {""},
                optionC = question.options.getOrElse(2) {""},
                optionD = question.options.getOrElse(3) {""},
                correctAnswer = question.correctAnswer,
                explanation = question.explanation,
                questionNumber = question.questionNumber
            )
        }

        pdfDocumentDao.insertObjectiveQuestions(entities)
    }

    suspend fun getObjectiveQuestions(documentId: String):List<ObjectiveQuestion>{
        return pdfDocumentDao.getObjectiveQuestions(documentId).map { it.toDomainModel() }
    }

    // ==================== Theory Questions ====================
    suspend fun saveTheoryQuestions(
        documentId: String,
        questions: List<TheoryQuestion>
    ){
        val entities = questions.map { question ->
            TheoryQuestionEntity(
                id= question.id,
                documentId= documentId,
                question= question.question,
                answer = question.answer,
                questionNumber = question.questionNumber
            )
        }
        pdfDocumentDao.insertTheoryQuestions(entities)
    }

    suspend fun getTheoryQuestions(documentId: String) : List<TheoryQuestion>{
        return pdfDocumentDao.getTheoryQuestions(documentId).map { it.toDomainModel() }
    }

    // ==================== Quiz Questions ====================
    suspend fun saveQuizQuestions(
        documentId: String,
        questions: List<QuizQuestion>
    ){
        val entities = questions.map { question ->
            QuizQuestionEntity(
                id = question.id,
                documentId = documentId,
                question=question.question,
                optionA = question.options.getOrElse(0) {""},
                optionB = question.options.getOrElse(1) {""},
                optionC = question.options.getOrElse(2) {""},
                optionD = question.options.getOrElse(3) {""},
                correctAnswer = question.correctAnswer,
                explanation = question.explanation,
                questionNumber = question.questionNumber,
            )
        }
        pdfDocumentDao.insertQuizQuestions((entities))
    }
    suspend fun getQuizQuestions(documentId: String) : List<QuizQuestion>{
        return pdfDocumentDao.getQuizQuestions(documentId).map { it.toDomainModel() }
    }

    // ==================== Mapping Functions ====================
    private fun ObjectiveQuestionEntity.toDomainModel(): ObjectiveQuestion{
        val mappedCorrectAnswer = when (correctAnswer.trim().uppercase()) {
            "A" -> optionA
            "B" -> optionB
            "C" -> optionC
            "D" -> optionD
            else -> correctAnswer
        }
        return ObjectiveQuestion(
            id = id,
            question = question,
            options = listOf(optionA, optionB, optionC, optionD),
            correctAnswer = mappedCorrectAnswer,
            explanation= explanation,
            questionNumber = questionNumber
        )
    }

    private fun TheoryQuestionEntity.toDomainModel(): TheoryQuestion{
        return TheoryQuestion(
            id = id,
            question= question,
            answer= answer,
            questionNumber = questionNumber
        )
    }

    private fun QuizQuestionEntity.toDomainModel(): QuizQuestion{
        val mappedCorrectAnswer = when (correctAnswer.trim().uppercase()) {
            "A" -> optionA
            "B" -> optionB
            "C" -> optionC
            "D" -> optionD
            else -> correctAnswer
        }
        return QuizQuestion(
            id = id,
            question = question,
            options = listOf(optionA, optionB, optionC, optionD),
            correctAnswer = mappedCorrectAnswer,
            explanation= explanation,
            questionNumber = questionNumber
        )
    }
}