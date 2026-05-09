package com.chaos.devoco.di

import android.content.Context
import androidx.room.Room
import com.chaos.devoco.data.local.datastore.UserPreferences
import com.chaos.devoco.data.local.db.AppDatabase
import com.chaos.devoco.data.local.db.dao.PdfDocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) : AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "devoco_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePdfDocumentDao(database: AppDatabase) : PdfDocumentDao{
        return database.pdfDocumentDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences{
        return UserPreferences(context)
    }
}