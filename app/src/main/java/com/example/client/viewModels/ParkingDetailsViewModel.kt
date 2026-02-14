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

data class ParkingDetailsUiState(
    val likeCount: Long = 0L,
    val likedByMe: Boolean = false,
    val likeLoading: Boolean = false,
    val likeError: String? = null
)

@HiltViewModel
class ParkingDetailsViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkingDetailsUiState())
    val uiState: StateFlow<ParkingDetailsUiState> = _uiState.asStateFlow()

    private var currentSpaceId: Long? = null

    fun setSpace(space: SpaceResponse) {
        if (currentSpaceId == space.id) return
        currentSpaceId = space.id
        loadLikeInfo()
    }

    fun loadLikeInfo() {
        val spaceId = currentSpaceId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(likeLoading = true, likeError = null)
            spaceRepository.getLike(spaceId)
                .onSuccess { like ->
                    _uiState.value = _uiState.value.copy(
                        likeCount = like.likeCount,
                        likedByMe = like.likedByMe,
                        likeLoading = false,
                        likeError = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        likeLoading = false,
                        likeError = it.message
                    )
                }
        }
    }

    fun toggleLike() {
        val spaceId = currentSpaceId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(likeLoading = true, likeError = null)
            val result = if (_uiState.value.likedByMe) {
                spaceRepository.unlike(spaceId)
            } else {
                spaceRepository.like(spaceId)
            }
            result
                .onSuccess { like ->
                    _uiState.value = _uiState.value.copy(
                        likeCount = like.likeCount,
                        likedByMe = like.likedByMe,
                        likeLoading = false,
                        likeError = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        likeLoading = false,
                        likeError = it.message
                    )
                }
        }
    }
}
