package com.chaos.devoco.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "theory_questions",
    foreignKeys = [
        ForeignKey(
            entity = PdfDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("documentId")]
)

data class TheoryQuestionEntity(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val question: String,
    val answer: String,
    val questionNumber: Int
)