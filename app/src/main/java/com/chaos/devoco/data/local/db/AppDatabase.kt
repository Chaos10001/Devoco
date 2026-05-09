package com.chaos.devoco.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chaos.devoco.data.local.db.dao.PdfDocumentDao
import com.chaos.devoco.data.local.db.entity.ObjectiveQuestionEntity
import com.chaos.devoco.data.local.db.entity.PdfDocumentEntity
import com.chaos.devoco.data.local.db.entity.QuizQuestionEntity
import com.chaos.devoco.data.local.db.entity.TheoryQuestionEntity

@Database(
    entities = [
        PdfDocumentEntity::class,
        ObjectiveQuestionEntity::class,
        TheoryQuestionEntity::class,
        QuizQuestionEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase(){
    abstract fun pdfDocumentDao() : PdfDocumentDao
}