package com.chaos.devoco.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_documents")
data class PdfDocumentEntity(
    @PrimaryKey
    val id: String,
    val fileName: String,
    val fileUri: String,
    val extractedText: String,
    val pageCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)