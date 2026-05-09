package com.chaos.devoco.domain.model

data class PdfDocument (
    val id: String,
    val fileName: String,
    val fileUri: String,
    val extractedText: String,
    val pageCount: Int,
)