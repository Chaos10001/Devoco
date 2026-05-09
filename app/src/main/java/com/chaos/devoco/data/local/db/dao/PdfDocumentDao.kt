package com.chaos.devoco.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chaos.devoco.data.local.db.entity.ObjectiveQuestionEntity
import com.chaos.devoco.data.local.db.entity.PdfDocumentEntity
import com.chaos.devoco.data.local.db.entity.QuizQuestionEntity
import com.chaos.devoco.data.local.db.entity.TheoryQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDocumentDao {
    // ==================== PDF Documents ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: PdfDocumentEntity)

    @Query("SELECT * FROM pdf_documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<PdfDocumentEntity>>

    @Query("SELECT * FROM pdf_documents WHERE id =:docId")
    suspend fun getDocumentById(docId: String): PdfDocumentEntity?

    @Query("DELETE FROM pdf_documents WHERE id = :docId")
    suspend fun deleteDocument(docId: String)

    // ==================== Objective Questions ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjectiveQuestions(questions: List<ObjectiveQuestionEntity>)

    @Query("SELECT * FROM objective_questions WHERE documentId = :docId ORDER BY questionNumber")
    suspend fun getObjectiveQuestions(docId: String) : List<ObjectiveQuestionEntity>

    @Query("DELETE FROM objective_questions WHERE documentId = :docId")
    suspend fun deleteObjectiveQuestions(docId: String)

    // ==================== Theory Questions ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheoryQuestions(questions: List<TheoryQuestionEntity>)

    @Query("SELECT * FROM theory_questions WHERE documentId = :docId ORDER BY questionNumber")
    suspend fun getTheoryQuestions(docId: String) : List<TheoryQuestionEntity>

    @Query("DELETE FROM theory_questions WHERE documentId = :docId")
    suspend fun deleteTheoryQuestions(docId: String)

    // ==================== Quiz Questions (20 Q/A) ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestionEntity>)

    @Query("SELECT * FROM quiz_questions WHERE documentId = :docId ORDER BY questionNumber")
    suspend fun getQuizQuestions(docId: String) : List<QuizQuestionEntity>

    @Query("DELETE FROM quiz_questions WHERE documentId = :docId")
    suspend fun deleteQuizQuestions(docId: String)
}