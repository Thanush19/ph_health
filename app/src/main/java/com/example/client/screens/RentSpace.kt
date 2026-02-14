package com.example.client.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.client.ui.components.ParkingPrimaryButton
import com.example.client.ui.components.ParkingTextField
import com.example.client.viewModels.RentSpaceEvent
import com.example.client.viewModels.RentSpaceUiEvent
import com.example.client.viewModels.RentSpaceViewModel
import java.io.File
import java.io.FileOutputStream

private const val MAX_IMAGES = 3

@Composable
fun RentSpace(
    onNavigateBack: () -> Unit = {},
    viewModel: RentSpaceViewModel = hiltViewModel()
) {
    val uiModel by viewModel.uiModel.collectAsState(initial = RentSpaceViewModel.InitialUiModel)
    val context = LocalContext.current
    var cameraFile by remember { mutableStateOf<File?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiModel.event) {
        when (uiModel.event) {
            is RentSpaceUiEvent.NavigateBack -> {
                onNavigateBack()
                viewModel.clearNavigationEvent()
            }
            null -> {}
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (uiModel.imagePaths.size >= MAX_IMAGES) return@let
            val tempFile = File(context.cacheDir, "space_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
            }
            viewModel.takeEvent(RentSpaceEvent.ImageAdded(tempFile.absolutePath))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        cameraFile?.let { file ->
            if (success && file.exists()) viewModel.takeEvent(RentSpaceEvent.ImageAdded(file.absolutePath))
            cameraFile = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val f = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
            cameraFile = f
            cameraLauncher.launch(
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f)
            )
        }
    }

    fun launchCamera() {
        if (uiModel.imagePaths.size >= MAX_IMAGES) return
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun launchGallery() {
        if (uiModel.imagePaths.size >= MAX_IMAGES) return
        galleryLauncher.launch("image/*")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        RentSpaceTopBar(onBackClick = onNavigateBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
        Text(
            text = "List your space",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rent out your land for parking",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        ParkingTextField(
            value = uiModel.address,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.AddressChanged(it)) },
            label = "Address",
            placeholder = "Full address of the space",
            singleLine = false,
            maxLines = 2,
            enabled = !uiModel.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParkingTextField(
            value = uiModel.squareFeet,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.SquareFeetChanged(it)) },
            label = "Square feet",
            placeholder = "e.g. 150",
            enabled = !uiModel.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParkingTextField(
            value = uiModel.vehicleTypes,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.VehicleTypesChanged(it)) },
            label = "Vehicle types",
            placeholder = "e.g. Car, Bike, SUV",
            enabled = !uiModel.isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Rent (set at least one)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        ParkingTextField(
            value = uiModel.rentPerHour,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.RentPerHourChanged(it)) },
            label = "Per hour (Rs.)",
            placeholder = "0 or leave empty",
            enabled = !uiModel.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        ParkingTextField(
            value = uiModel.rentPerDay,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.RentPerDayChanged(it)) },
            label = "Per day (Rs.)",
            placeholder = "0 or leave empty",
            enabled = !uiModel.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        ParkingTextField(
            value = uiModel.rentMonthly,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.RentMonthlyChanged(it)) },
            label = "Monthly (Rs.)",
            placeholder = "0 or leave empty",
            enabled = !uiModel.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Photos (up to $MAX_IMAGES)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiModel.imagePaths.forEachIndexed { index, path ->
                Card(
                    modifier = Modifier.size(96.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Space photo ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.takeEvent(RentSpaceEvent.RemoveImage(index)) },
                            modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                        ) {
                            Text("×", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            if (uiModel.imagePaths.size < MAX_IMAGES) {
                OutlinedButton(
                    onClick = { showUploadDialog = true },
                    modifier = Modifier.height(48.dp),
                    enabled = !uiModel.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Add photo", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (showUploadDialog) {
            AlertDialog(
                onDismissRequest = { showUploadDialog = false },
                confirmButton = {},
                title = { Text("Add photo", style = MaterialTheme.typography.titleMedium) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = {
                                showUploadDialog = false
                                launchGallery()
                            }
                        ) {
                            Text("From gallery", color = MaterialTheme.colorScheme.onSurface)
                        }
                        TextButton(
                            onClick = {
                                showUploadDialog = false
                                launchCamera()
                            }
                        ) {
                            Text("Take photo", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUploadDialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }

        if (uiModel.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiModel.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ParkingPrimaryButton(
            text = "List space",
            onClick = { viewModel.takeEvent(RentSpaceEvent.Submit) },
            enabled = !uiModel.isLoading,
            loading = uiModel.isLoading
        )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RentSpaceTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("List your space", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
