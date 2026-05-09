package com.chaos.devoco.data.repository

import android.content.Context
import android.net.Uri
import com.chaos.devoco.data.local.db.dao.PdfDocumentDao
import com.chaos.devoco.data.local.db.entity.PdfDocumentEntity
import com.chaos.devoco.domain.model.PdfDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepository @Inject constructor(
    private val pdfDocumentDao: PdfDocumentDao,
    @ApplicationContext private val context: Context
){
    fun getAllDocuments(): Flow<List<PdfDocument>>{
        return pdfDocumentDao.getAllDocuments().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getDocumentById(docId: String): PdfDocument?{
        return pdfDocumentDao.getDocumentById(docId)?.toDomainModel()
    }

    suspend fun saveDocument(
        uri: Uri,
        fileName: String,
        extractedText:String,
        pageCount:Int
    ): PdfDocument{
        val entity = PdfDocumentEntity(
            id = UUID.randomUUID().toString(),
            fileName= fileName,
            fileUri = uri.toString(),
            extractedText = extractedText,
            pageCount = pageCount
        )
        pdfDocumentDao.insertDocument(entity)
        return entity.toDomainModel()
    }

    suspend fun deleteDocument(docId: String){
        // Delete all associated question first
        pdfDocumentDao.deleteQuizQuestions(docId)
        pdfDocumentDao.deleteTheoryQuestions(docId)
        pdfDocumentDao.deleteObjectiveQuestions(docId)

        // Then delete the document
        pdfDocumentDao.deleteDocument(docId)
    }

    private fun PdfDocumentEntity.toDomainModel(): PdfDocument{
        return PdfDocument(
            id=id,
            fileName= fileName,
            fileUri= fileUri,
            extractedText= extractedText,
            pageCount= pageCount
        )
    }
}