package com.example.client.viewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.example.client.data.model.CreateSpaceRequest
import com.example.client.data.repository.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

private const val MAX_IMAGES = 3

@HiltViewModel
class RentSpaceViewModel @Inject constructor(
    val presenter: RentSpacePresenter
) : ViewModel() {

    val uiModel: StateFlow<RentSpaceUiModel> = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        presenter.generateUi()
    }

    fun takeEvent(event: RentSpaceEvent) {
        presenter.onInteraction(event)
    }

    fun clearNavigationEvent() {
        presenter.clearNavigationEvent()
    }

    companion object {
        val InitialUiModel = RentSpaceUiModel(
            address = "",
            squareFeet = "",
            vehicleTypes = "",
            rentPerHour = "",
            rentPerDay = "",
            rentMonthly = "",
            imagePaths = emptyList(),
            imageUploadUrls = emptyList(),
            isLoading = false,
            errorMessage = null,
            event = null
        )
    }
}

class RentSpacePresenter @Inject constructor(
    private val spaceRepository: SpaceRepository
) {
    private val events = Channel<RentSpaceEvent>(Channel.UNLIMITED)
    private val state = RentSpaceState()

    fun onInteraction(event: RentSpaceEvent) {
        events.trySend(event)
    }

    fun clearNavigationEvent() {
        state.event = null
    }

    @Composable
    internal fun generateUi(): RentSpaceUiModel {
        HandleEvents()
        return state.toUi()
    }

    @Composable
    private fun HandleEvents() {
        LaunchedEffect(Unit) {
            events.receiveAsFlow().collect { event ->
                when (event) {
                    is RentSpaceEvent.AddressChanged -> state.address = event.value
                    is RentSpaceEvent.SquareFeetChanged -> state.squareFeet = event.value
                    is RentSpaceEvent.VehicleTypesChanged -> state.vehicleTypes = event.value
                    is RentSpaceEvent.RentPerHourChanged -> state.rentPerHour = event.value
                    is RentSpaceEvent.RentPerDayChanged -> state.rentPerDay = event.value
                    is RentSpaceEvent.RentMonthlyChanged -> state.rentMonthly = event.value
                    is RentSpaceEvent.ImageAdded -> {
                        if (state.imagePaths.size < MAX_IMAGES) {
                            state.imagePaths = state.imagePaths + event.filePath
                        }
                    }
                    is RentSpaceEvent.RemoveImage -> {
                        val index = event.index
                        if (index in state.imagePaths.indices) {
                            state.imagePaths = state.imagePaths.toMutableList().apply { removeAt(index) }
                            if (index < state.imageUploadUrls.size) {
                                state.imageUploadUrls = state.imageUploadUrls.toMutableList().apply { removeAt(index) }
                            }
                        }
                    }
                    is RentSpaceEvent.Submit -> handleSubmit()
                }
            }
        }
    }

    private suspend fun handleSubmit() {
        state.isLoading = true
        state.errorMessage = null
        state.event = null

        val sqFt = state.squareFeet.toIntOrNull()
        if (sqFt == null || sqFt <= 0) {
            state.errorMessage = "Enter valid square feet"
            state.isLoading = false
            return
        }
        if (state.address.isBlank()) {
            state.errorMessage = "Enter address"
            state.isLoading = false
            return
        }
        if (state.vehicleTypes.isBlank()) {
            state.errorMessage = "Enter vehicle types (e.g. Car, Bike)"
            state.isLoading = false
            return
        }

        val perHour = state.rentPerHour.toDoubleOrNull()?.takeIf { it > 0 }
        val perDay = state.rentPerDay.toDoubleOrNull()?.takeIf { it > 0 }
        val monthly = state.rentMonthly.toDoubleOrNull()?.takeIf { it > 0 }
        if (perHour == null && perDay == null && monthly == null) {
            state.errorMessage = "Set at least one rent option (hour, day, or monthly)"
            state.isLoading = false
            return
        }

        val paths = state.imagePaths
        val uploadedUrls = state.imageUploadUrls.toMutableList()
        for (i in paths.indices) {
            if (i < uploadedUrls.size) continue
            val file = java.io.File(paths[i])
            if (!file.exists()) continue
            spaceRepository.uploadImage(file)
                .onSuccess { uploadedUrls.add(it.url) }
                .onFailure {
                    state.errorMessage = it.message ?: "Image upload failed"
                    state.isLoading = false
                    return
                }
        }
        state.imageUploadUrls = uploadedUrls

        val request = CreateSpaceRequest(
            address = state.address.trim(),
            squareFeet = sqFt,
            vehicleTypes = state.vehicleTypes.trim(),
            rentPerHour = perHour,
            rentPerDay = perDay,
            rentMonthly = monthly,
            imageUrls = uploadedUrls.ifEmpty { null }
        )

        spaceRepository.createSpace(request)
            .onSuccess {
                state.isLoading = false
                state.event = RentSpaceUiEvent.NavigateBack
            }
            .onFailure {
                state.errorMessage = it.message ?: "Failed to create listing"
                state.isLoading = false
            }
    }
}

sealed class RentSpaceEvent {
    data class AddressChanged(val value: String) : RentSpaceEvent()
    data class SquareFeetChanged(val value: String) : RentSpaceEvent()
    data class VehicleTypesChanged(val value: String) : RentSpaceEvent()
    data class RentPerHourChanged(val value: String) : RentSpaceEvent()
    data class RentPerDayChanged(val value: String) : RentSpaceEvent()
    data class RentMonthlyChanged(val value: String) : RentSpaceEvent()
    data class ImageAdded(val filePath: String) : RentSpaceEvent()
    data class RemoveImage(val index: Int) : RentSpaceEvent()
    data object Submit : RentSpaceEvent()
}

@Stable
internal class RentSpaceState {
    var address by mutableStateOf("")
    var squareFeet by mutableStateOf("")
    var vehicleTypes by mutableStateOf("")
    var rentPerHour by mutableStateOf("")
    var rentPerDay by mutableStateOf("")
    var rentMonthly by mutableStateOf("")
    var imagePaths by mutableStateOf<List<String>>(emptyList())
    var imageUploadUrls by mutableStateOf<List<String>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var event by mutableStateOf<RentSpaceUiEvent?>(null)

    internal fun toUi(): RentSpaceUiModel = RentSpaceUiModel(
        address = address,
        squareFeet = squareFeet,
        vehicleTypes = vehicleTypes,
        rentPerHour = rentPerHour,
        rentPerDay = rentPerDay,
        rentMonthly = rentMonthly,
        imagePaths = imagePaths,
        imageUploadUrls = imageUploadUrls,
        isLoading = isLoading,
        errorMessage = errorMessage,
        event = event
    )
}

@Immutable
data class RentSpaceUiModel(
    val address: String,
    val squareFeet: String,
    val vehicleTypes: String,
    val rentPerHour: String,
    val rentPerDay: String,
    val rentMonthly: String,
    val imagePaths: List<String>,
    val imageUploadUrls: List<String>,
    val isLoading: Boolean,
    val errorMessage: String?,
    val event: RentSpaceUiEvent?
)

@Stable
sealed interface RentSpaceUiEvent {
    data object NavigateBack : RentSpaceUiEvent
}
