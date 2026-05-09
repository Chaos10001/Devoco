package com.chaos.devoco.util

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject

data class ExtractionResult(
    val text:String,
    val pageCount: Int
)

class PdfExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        PDFBoxResourceLoader.init(context)
    }

    suspend fun extractText(uri: Uri): ExtractionResult = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("Cannot open PDF file")

        val document = PDDocument.load(inputStream)
        val stripper = PDFTextStripper()

        val text = stripper.getText(document)
        val pageCount = document.numberOfPages

        document.close()
        inputStream.close()

        if (text.isBlank()){
            throw IllegalStateException("No text could be extracted from pdf")
        }

        ExtractionResult(
            text = text.trim(),
            pageCount = pageCount
        )
    }

    suspend fun getFileName(uri: Uri) : String = withContext(Dispatchers.IO) {
        when (uri.scheme){
            "file" -> uri.lastPathSegment ?: "Unknown.pdf"
            "content" -> {
                val cursor = context.contentResolver.query(uri, null, null,null,null)
                cursor?.use{
                    if (it.moveToFirst()){
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0){
                            return@withContext it.getString(nameIndex)
                        }
                    }
                }
                "Unknown.pdf"
            }
            else -> "Unknown.pdf"
        }
    }
}