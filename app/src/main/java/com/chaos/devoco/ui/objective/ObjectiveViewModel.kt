package com.chaos.devoco.ui.objective

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaos.devoco.data.repository.QuestionRepository
import com.chaos.devoco.domain.model.ObjectiveQuestion
import com.chaos.devoco.domain.usecase.GenerateObjectiveQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ObjectiveUiState(
    val questions: List<ObjectiveQuestion> = emptyList(),
    val revealedAnswers: Set<Int> = emptySet(),
    val isLoading:Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ObjectiveViewModel @Inject constructor(
    private val generateObjectiveQuestionsUseCase: GenerateObjectiveQuestionsUseCase,
    private val questionRepository: QuestionRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(ObjectiveUiState())
    val uiState: StateFlow<ObjectiveUiState> = _uiState.asStateFlow()

    fun loadQuestions(documentId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error= null) }

            val localQuestion= questionRepository.getObjectiveQuestions(documentId)

            if (localQuestion.isNotEmpty()){
                _uiState.update {
                    it.copy(
                        questions = localQuestion,
                        isLoading = false
                    )
                }
            }else{
                // Generate from Api
                generateObjectiveQuestionsUseCase(documentId).fold(
                    onSuccess = { questions ->
                        _uiState.update {
                            it.copy(
                                questions= questions,
                                isLoading= false
                            )
                        }
                    },
                    onFailure = {error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error= error.message ?: "Failed to generate questions"
                            )
                        }
                        println(error.message)
                    }
                )
            }
        }
    }
    fun toggleReveal(index: Int){
        _uiState.update { state ->
            val newRevealed= state.revealedAnswers.toMutableSet()
            if (newRevealed.contains(index)){
                newRevealed.remove(index)
            } else{
                newRevealed.add(index)
            }
            state.copy(revealedAnswers = newRevealed)
        }
    }
}

