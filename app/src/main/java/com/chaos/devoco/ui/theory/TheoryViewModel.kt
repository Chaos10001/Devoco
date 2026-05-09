package com.chaos.devoco.ui.theory

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaos.devoco.data.repository.QuestionRepository
import com.chaos.devoco.domain.model.TheoryQuestion
import com.chaos.devoco.domain.usecase.GenerateTheoryQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TheoryUiState(
    val questions: List<TheoryQuestion> = emptyList(),
    val revealedAnswers: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error:String? = null
)

@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val generateTheoryQuestionsUseCase: GenerateTheoryQuestionsUseCase,
    private val questionRepository: QuestionRepository
) : ViewModel(){
    private val _uiState= MutableStateFlow(TheoryUiState())
    val uiState: StateFlow<TheoryUiState> = _uiState.asStateFlow()

    fun loadQuestions(documentId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val localQuestions= questionRepository.getTheoryQuestions(documentId)

            if (localQuestions.isNotEmpty()){
                _uiState.update {
                    it.copy(
                        questions = localQuestions,
                        isLoading = false
                    )
                }
            }else{
                generateTheoryQuestionsUseCase(documentId).fold(
                    onSuccess = {questions ->
                        _uiState.update {
                            it.copy(
                                questions = questions,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = {error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to generate questions"
                            )
                        }
                    }
                )
            }
        }
    }

    fun toggleReveal(index:Int){
        _uiState.update { state->
            val newRevealed= state.revealedAnswers.toMutableSet()
            if (newRevealed.contains(index)){
                newRevealed.remove(index)
            }else{
                newRevealed.add(index)
            }
            state.copy(revealedAnswers = newRevealed)
        }
    }
}