package com.example.client.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
            null -> { }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (uiModel.imagePaths.size >= MAX_IMAGES) return@let
            val tempFile = File(context.cacheDir, "space_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            viewModel.takeEvent(RentSpaceEvent.ImageAdded(tempFile.absolutePath))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        cameraFile?.let { file ->
            if (success && file.exists()) {
                viewModel.takeEvent(RentSpaceEvent.ImageAdded(file.absolutePath))
            }
            cameraFile = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val f = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
            cameraFile = f
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                f
            )
            cameraLauncher.launch(uri)
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Rent Your Space",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = uiModel.address,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.AddressChanged(it)) },
            label = { Text("Address") },
            placeholder = { Text("Full address of the space") },
            singleLine = false,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiModel.isLoading,
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiModel.squareFeet,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.SquareFeetChanged(it)) },
            label = { Text("Square feet") },
            placeholder = { Text("e.g. 150") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiModel.isLoading,
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiModel.vehicleTypes,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.VehicleTypesChanged(it)) },
            label = { Text("Vehicle types") },
            placeholder = { Text("e.g. Car, Bike, SUV") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiModel.isLoading,
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Rent (set at least one)",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiModel.rentPerHour,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.RentPerHourChanged(it)) },
            label = { Text("Per hour") },
            placeholder = { Text("0 or leave empty") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiModel.isLoading,
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiModel.rentPerDay,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.RentPerDayChanged(it)) },
            label = { Text("Per day") },
            placeholder = { Text("0 or leave empty") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiModel.isLoading,
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiModel.rentMonthly,
            onValueChange = { viewModel.takeEvent(RentSpaceEvent.RentMonthlyChanged(it)) },
            label = { Text("Monthly") },
            placeholder = { Text("0 or leave empty") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiModel.isLoading,
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Photos (up to $MAX_IMAGES)",
            style = MaterialTheme.typography.titleSmall,
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                        ) {
                            Text("×", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            if (uiModel.imagePaths.size < MAX_IMAGES) {
                Button(
                    onClick = { showUploadDialog = true },
                    modifier = Modifier.height(40.dp),
                    enabled = !uiModel.isLoading
                ) {
                    Text("Upload image")
                }
            }
        }

        if (showUploadDialog) {
            AlertDialog(
                onDismissRequest = { showUploadDialog = false },
                confirmButton = {},
                title = { Text("Upload a photo") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = {
                                showUploadDialog = false
                                launchGallery()
                            }
                        ) {
                            Text("Photo from gallery")
                        }
                        TextButton(
                            onClick = {
                                showUploadDialog = false
                                launchCamera()
                            }
                        ) {
                            Text("Capture image")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUploadDialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
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

        Button(
            onClick = { viewModel.takeEvent(RentSpaceEvent.Submit) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiModel.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (uiModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Submit listing", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
    cursorColor = MaterialTheme.colorScheme.primary
)
