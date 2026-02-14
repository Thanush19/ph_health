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
import java.util.Calendar
import javax.inject.Inject

/** Simple date (year, month 1-12, day). */
data class SimpleDate(val year: Int, val month: Int, val day: Int) {
    fun toIsoDateString(): String =
        "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

data class BookSlotUiState(
    val slotStart: String = "",
    val slotEnd: String = "",
    val displayYear: Int = 0,
    val displayMonth: Int = 0,
    val selectedStartDate: SimpleDate? = null,
    val selectedEndDate: SimpleDate? = null,
    val startTime: String = "09:00",
    val endTime: String = "17:00",
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

    private val calendar: Calendar = Calendar.getInstance()

    init {
        calendar.timeInMillis = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(
            displayYear = calendar.get(Calendar.YEAR),
            displayMonth = calendar.get(Calendar.MONTH) + 1
        )
    }

    fun setSpace(space: SpaceResponse) {
        currentSpace = space
    }

    fun previousMonth() {
        val m = _uiState.value.displayMonth
        val y = _uiState.value.displayYear
        if (m <= 1) _uiState.value = _uiState.value.copy(displayYear = y - 1, displayMonth = 12)
        else _uiState.value = _uiState.value.copy(displayMonth = m - 1)
    }

    fun nextMonth() {
        val m = _uiState.value.displayMonth
        val y = _uiState.value.displayYear
        if (m >= 12) _uiState.value = _uiState.value.copy(displayYear = y + 1, displayMonth = 1)
        else _uiState.value = _uiState.value.copy(displayMonth = m + 1)
    }

    fun selectDate(year: Int, month: Int, day: Int) {
        val start = _uiState.value.selectedStartDate
        val end = _uiState.value.selectedEndDate
        val date = SimpleDate(year, month, day)
        _uiState.value = when {
            start == null -> _uiState.value.copy(selectedStartDate = date, error = null)
            end == null -> {
                if (date.toIsoDateString() < start.toIsoDateString())
                    _uiState.value.copy(selectedStartDate = date, selectedEndDate = start, error = null)
                else
                    _uiState.value.copy(selectedEndDate = date, error = null)
            }
            else -> _uiState.value.copy(selectedStartDate = date, selectedEndDate = null, error = null)
        }
    }

    fun setStartTime(value: String) {
        _uiState.value = _uiState.value.copy(startTime = value, error = null)
    }

    fun setEndTime(value: String) {
        _uiState.value = _uiState.value.copy(endTime = value, error = null)
    }

    fun createBooking() {
        val space = currentSpace ?: return
        val start = _uiState.value.selectedStartDate ?: return
        val end = _uiState.value.selectedEndDate ?: start
        val st = _uiState.value.startTime.ifBlank { "09:00" }
        val et = _uiState.value.endTime.ifBlank { "17:00" }
        val slotStartStr = "${start.toIsoDateString()}T${st}:00"
        val slotEndStr = "${end.toIsoDateString()}T${et}:00"
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(bookingLoading = true, error = null)
            bookingRepository.createBooking(space.id, slotStartStr, slotEndStr)
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
