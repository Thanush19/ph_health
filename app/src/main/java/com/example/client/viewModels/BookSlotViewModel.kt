package com.example.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.data.model.BookingResponse
import com.example.client.data.model.SpaceResponse
import com.example.client.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookSlotUiState(
    val slotStart: String = "",
    val slotEnd: String = "",
    val booking: BookingResponse? = null,
    val bookingLoading: Boolean = false,
    val paymentLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BookSlotViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookSlotUiState())
    val uiState: StateFlow<BookSlotUiState> = _uiState.asStateFlow()

    private var currentSpace: SpaceResponse? = null

    fun setSpace(space: SpaceResponse) {
        currentSpace = space
    }

    fun setSlotStart(value: String) {
        _uiState.value = _uiState.value.copy(slotStart = value, error = null)
    }

    fun setSlotEnd(value: String) {
        _uiState.value = _uiState.value.copy(slotEnd = value, error = null)
    }

    fun createBooking() {
        val space = currentSpace ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(bookingLoading = true, error = null)
            bookingRepository.createBooking(space.id, _uiState.value.slotStart, _uiState.value.slotEnd)
                .onSuccess { booking ->
                    _uiState.value = _uiState.value.copy(
                        booking = booking,
                        bookingLoading = false,
                        error = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        bookingLoading = false,
                        error = it.message
                    )
                }
        }
    }

    fun payDummy() {
        val bookingId = _uiState.value.booking?.id ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(paymentLoading = true, error = null)
            bookingRepository.payDummy(bookingId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        booking = _uiState.value.booking?.copy(paymentStatus = "PAID"),
                        paymentLoading = false,
                        error = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        paymentLoading = false,
                        error = it.message
                    )
                }
        }
    }
}
