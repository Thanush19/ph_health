package com.example.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.data.model.SpaceResponse
import com.example.client.data.repository.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FindParkingUiState(
    val spaces: List<SpaceResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FindParkingViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FindParkingUiState())
    val uiState: StateFlow<FindParkingUiState> = _uiState.asStateFlow()

    init {
        loadSpaces()
    }

    fun loadSpaces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            spaceRepository.getSpaces()
                .onSuccess { list ->
                    _uiState.value = _uiState.value.copy(
                        spaces = list,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "Failed to load spaces"
                    )
                }
        }
    }
}
