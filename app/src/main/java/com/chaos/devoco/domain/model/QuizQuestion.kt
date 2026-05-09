package com.chaos.devoco.domain.model

data class QuizQuestion (
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val questionNumber: Int,
    val userAnswer: String? =null
)