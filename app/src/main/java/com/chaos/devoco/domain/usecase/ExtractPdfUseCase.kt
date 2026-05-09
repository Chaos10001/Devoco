package com.chaos.devoco.domain.usecase

import android.content.Context
import android.net.Uri
import com.chaos.devoco.data.repository.PdfRepository
import com.chaos.devoco.util.PdfExtractor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExtractPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfRepository: PdfRepository,
    private val pdfExtractor: PdfExtractor
){
    suspend operator fun invoke(uri: Uri) : Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val extractionResult = pdfExtractor.extractText(uri)

            if (extractionResult.text.isEmpty()){
                Result.failure(
                    IllegalStateException("PDF contains no text.")
                )
            } else {
                pdfRepository.saveDocument(
                    uri = uri,
                    fileName = pdfExtractor.getFileName(uri),
                    extractedText = extractionResult.text,
                    pageCount = extractionResult.pageCount
                )
                Result.success(Unit)
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getFileName(uri: Uri) : String {
        return pdfExtractor.getFileName(uri)
    }
}