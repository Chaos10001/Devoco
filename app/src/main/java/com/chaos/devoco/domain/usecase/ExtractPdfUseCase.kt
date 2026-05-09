package com.chaos.devoco.domain.usecase

import android.content.Context
import android.net.Uri
import com.chaos.devoco.data.repository.PdfRepository
import com.chaos.devoco.util.PdfExtractor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExtractPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfRepository: PdfRepository,
    private val pdfExtractor: PdfExtractor
){
    suspend operator fun invoke(uri: Uri) : Result<Unit>{
        return try {
            val extractionResult = pdfExtractor.extractText(uri)

            if (extractionResult.text.isEmpty()){
                return Result.failure(
                    IllegalStateException("PDF contains no text.")
                )
            }

            pdfRepository.saveDocument(
                uri = uri,
                fileName = pdfExtractor.getFileName(uri),
                extractedText = extractionResult.text,
                pageCount = extractionResult.pageCount
            )
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    fun getFileName(uri: Uri) : String {
        return pdfExtractor.getFileName(uri)
    }
}