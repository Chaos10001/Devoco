package com.chaos.devoco.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaos.devoco.data.repository.QuestionRepository
import com.chaos.devoco.domain.model.QuizQuestion
import com.chaos.devoco.domain.usecase.GenerateQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val isLoading: Boolean = false,
    val showResult: Boolean = false,
    val answeredCount: Int = 0,
    val correctAnswer: Int = 0,
    val scorePercentage: Float = 0f,
    val error: String? = null
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val generateQuizUseCase: GenerateQuizUseCase,
    private val questionRepository: QuestionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(documentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val localQuestions = questionRepository.getQuizQuestions(documentId)

            if (localQuestions.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        questions = localQuestions,
                        isLoading = false,
                        answeredCount = localQuestions.count { q -> q.userAnswer != null }
                    )
                }
            } else {
                generateQuizUseCase(documentId).fold(
                    onSuccess = { questions ->
                        _uiState.update {
                            it.copy(
                                questions = questions,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to generate quiz"
                            )
                        }
                    }
                )
            }
        }
    }

    fun onAnswerSelected(questionIndex: Int, answer: String) {
        _uiState.update { state ->
            val updatedQuestion = state.questions.toMutableList()
            updatedQuestion[questionIndex] = updatedQuestion[questionIndex].copy(
                userAnswer = answer
            )
            state.copy(
                questions = updatedQuestion,
                answeredCount = updatedQuestion.count { it.userAnswer != null }
            )
        }
    }

    fun submitQuiz() {
        _uiState.update { state ->
            val correctCount = state.questions.count {
                val userAns = it.userAnswer?.trim() ?: ""
                val correctAns = it.correctAnswer.trim()
                userAns.equals(correctAns, ignoreCase = true)
            }

            state.copy(
                showResult = true,
                correctAnswer = correctCount,
                scorePercentage = if (state.questions.isNotEmpty()) {
                    (correctCount.toFloat() / state.questions.size) * 100f
                } else 0f
            )
        }
    }

    fun resetQuiz() {
        _uiState.update { state ->
            val resetQuestion = state.questions.map { it.copy(userAnswer = null) }

            state.copy(
                questions = resetQuestion,
                showResult = false,
                answeredCount = 0,
                correctAnswer = 0,
                scorePercentage = 0f
            )
        }
    }
}