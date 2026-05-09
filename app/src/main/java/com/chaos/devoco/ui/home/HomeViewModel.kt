package com.chaos.devoco.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaos.devoco.data.repository.PdfRepository
import com.chaos.devoco.domain.model.PdfDocument
import com.chaos.devoco.domain.usecase.ExtractPdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val documents: List<PdfDocument> = emptyList(),
    val isLoading:Boolean = false,
    val error: String? = null,
    val isDeleting: Boolean = false,
    val deleteSuccess: String? = null,
    val importSuccess: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val extractPdfUseCase: ExtractPdfUseCase,
    private val pdfRepository: PdfRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var processedUri: Uri? = null

    init{
        loadDocuments()
    }

    private fun loadDocuments(){
        viewModelScope.launch {
            pdfRepository.getAllDocuments().collect { documents ->
                _uiState.update { it.copy(
                    documents = documents,
                    isLoading = false
                ) }
            }
        }
    }

    fun onPdfSelected(uri: Uri){
        // Avoid processing the same URI multiple times
        if (uri == processedUri && _uiState.value.isLoading) return
        processedUri = uri

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            extractPdfUseCase(uri).fold(
                onSuccess = {
                    val fileName = extractPdfUseCase.getFileName(uri)
                    _uiState.update { it.copy(
                        isLoading = false,
                        importSuccess = "$fileName imported successfully"
                    ) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to import PDF"
                        )
                    }
                }
            )
        }
    }

    fun retryLastImport() {
        processedUri?.let { onPdfSelected(it) }
    }

    fun deleteDocument(document: PdfDocument){
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }

            try {
                pdfRepository.deleteDocument(document.id)
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteSuccess = "${document.fileName} deleted"
                    )
                }
            } catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        error = "Failed to delete: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError(){
        _uiState.update { it.copy(error= null)}
    }

    fun clearDeleteSuccess(){
        _uiState.update { it.copy(deleteSuccess = null) }
    }

    fun clearImportSuccess(){
        _uiState.update { it.copy(importSuccess = null) }
    }
}