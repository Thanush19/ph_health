package com.example.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.data.model.CreateSpaceRequest
import com.example.client.data.model.SpaceResponse
import com.example.client.data.repository.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_IMAGES = 3

data class EditSpaceUiState(
    val spaceId: Long = 0L,
    val address: String = "",
    val squareFeet: String = "",
    val vehicleTypes: String = "",
    val rentPerHour: String = "",
    val rentPerDay: String = "",
    val rentMonthly: String = "",
    val imageItems: List<String> = emptyList(), // URLs or file paths
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateBack: Boolean = false
)

@HiltViewModel
class EditSpaceViewModel @Inject constructor(
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditSpaceUiState())
    val uiState: StateFlow<EditSpaceUiState> = _uiState.asStateFlow()

    fun initFromSpace(space: SpaceResponse) {
        _uiState.value = EditSpaceUiState(
            spaceId = space.id,
            address = space.address,
            squareFeet = space.squareFeet.toString(),
            vehicleTypes = space.vehicleTypes,
            rentPerHour = space.rentPerHour?.toString()?.takeIf { it != "0.0" } ?: "",
            rentPerDay = space.rentPerDay?.toString()?.takeIf { it != "0.0" } ?: "",
            rentMonthly = space.rentMonthly?.toString()?.takeIf { it != "0.0" } ?: "",
            imageItems = space.imageUrls.orEmpty()
        )
    }

    fun updateAddress(v: String) { _uiState.value = _uiState.value.copy(address = v) }
    fun updateSquareFeet(v: String) { _uiState.value = _uiState.value.copy(squareFeet = v) }
    fun updateVehicleTypes(v: String) { _uiState.value = _uiState.value.copy(vehicleTypes = v) }
    fun updateRentPerHour(v: String) { _uiState.value = _uiState.value.copy(rentPerHour = v) }
    fun updateRentPerDay(v: String) { _uiState.value = _uiState.value.copy(rentPerDay = v) }
    fun updateRentMonthly(v: String) { _uiState.value = _uiState.value.copy(rentMonthly = v) }

    fun addImage(path: String) {
        val s = _uiState.value
        if (s.imageItems.size >= MAX_IMAGES) return
        _uiState.value = s.copy(imageItems = s.imageItems + path)
    }

    fun removeImage(index: Int) {
        val s = _uiState.value
        if (index !in s.imageItems.indices) return
        _uiState.value = s.copy(imageItems = s.imageItems.toMutableList().apply { removeAt(index) })
    }

    fun submit() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.spaceId == 0L) return@launch
            val sqFt = s.squareFeet.toIntOrNull() ?: 0
            if (sqFt <= 0) {
                _uiState.value = s.copy(errorMessage = "Enter valid square feet")
                return@launch
            }
            if (s.address.isBlank()) {
                _uiState.value = s.copy(errorMessage = "Enter address")
                return@launch
            }
            if (s.vehicleTypes.isBlank()) {
                _uiState.value = s.copy(errorMessage = "Enter vehicle types")
                return@launch
            }
            val perHour = s.rentPerHour.toDoubleOrNull()
            val perDay = s.rentPerDay.toDoubleOrNull()
            val monthly = s.rentMonthly.toDoubleOrNull()
            if ((perHour == null || perHour <= 0) && (perDay == null || perDay <= 0) && (monthly == null || monthly <= 0)) {
                _uiState.value = s.copy(errorMessage = "Set at least one rent option")
                return@launch
            }
            _uiState.value = s.copy(isLoading = true, errorMessage = null)
            val existingUrls = s.imageItems.filter { it.startsWith("http") }
            val pathsToUpload = s.imageItems.filter { !it.startsWith("http") }
            val newUrls = mutableListOf<String>()
            for (path in pathsToUpload) {
                val file = java.io.File(path)
                if (file.exists()) {
                    spaceRepository.uploadImage(file).onSuccess { newUrls.add(it.url) }
                        .onFailure { _uiState.value = s.copy(isLoading = false, errorMessage = "Image upload failed"); return@launch }
                }
            }
            val allUrls = existingUrls + newUrls
            val request = CreateSpaceRequest(
                address = s.address,
                squareFeet = sqFt,
                vehicleTypes = s.vehicleTypes,
                rentPerHour = perHour,
                rentPerDay = perDay,
                rentMonthly = monthly,
                imageUrls = allUrls.ifEmpty { null }
            )
            spaceRepository.updateSpace(s.spaceId, request)
                .onSuccess {
                    _uiState.value = s.copy(isLoading = false, navigateBack = true)
                }
                .onFailure {
                    _uiState.value = s.copy(isLoading = false, errorMessage = it.message ?: "Update failed")
                }
        }
    }

    fun clearNavigateBack() {
        _uiState.value = _uiState.value.copy(navigateBack = false)
    }
}
