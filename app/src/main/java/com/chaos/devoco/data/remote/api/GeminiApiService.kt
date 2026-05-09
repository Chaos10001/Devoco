package com.chaos.devoco.data.remote.api

import com.chaos.devoco.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiService @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
    )

    suspend fun generateContent(prompt : String): Result<String>{
        return try {
            val response = model.generateContent(prompt)
            Result.success(response.text ?: "")
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    fun generateContentStream(prompt: String) : Flow<String> = flow {
        val response= model.generateContentStream(prompt)
        response.collect { chunk ->
            emit(chunk.text ?: "")
        }
    }
}