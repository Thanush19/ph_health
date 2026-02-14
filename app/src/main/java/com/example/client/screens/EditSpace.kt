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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.client.data.model.SpaceResponse
import com.example.client.ui.components.ParkingPrimaryButton
import com.example.client.ui.components.ParkingTextField
import com.example.client.viewModels.EditSpaceViewModel
import java.io.File
import java.io.FileOutputStream

private const val MAX_IMAGES = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSpace(
    space: SpaceResponse?,
    onNavigateBack: () -> Unit = {},
    viewModel: EditSpaceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var cameraFile by remember { mutableStateOf<File?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(space) {
        space?.let { viewModel.initFromSpace(it) }
    }
    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.clearNavigateBack()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (uiState.imageItems.size >= MAX_IMAGES) return@let
            val tempFile = File(context.cacheDir, "edit_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
            }
            viewModel.addImage(tempFile.absolutePath)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        cameraFile?.let { file ->
            if (success && file.exists()) viewModel.addImage(file.absolutePath)
            cameraFile = null
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val f = File(context.cacheDir, "edit_capture_${System.currentTimeMillis()}.jpg")
            cameraFile = f
            cameraLauncher.launch(
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f)
            )
        }
    }

    if (space == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No space to edit", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        TopAppBar(
            title = { Text("Edit space", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Text("←", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
        Column(
            Modifier
                .weight(1f)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ParkingTextField(
                value = uiState.address,
                onValueChange = { viewModel.updateAddress(it) },
                label = "Address",
                placeholder = "Full address",
                singleLine = false,
                maxLines = 2,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(16.dp))
            ParkingTextField(
                value = uiState.squareFeet,
                onValueChange = { viewModel.updateSquareFeet(it) },
                label = "Square feet",
                placeholder = "e.g. 150",
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(16.dp))
            ParkingTextField(
                value = uiState.vehicleTypes,
                onValueChange = { viewModel.updateVehicleTypes(it) },
                label = "Vehicle types",
                placeholder = "e.g. Car, Bike",
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(20.dp))
            Text("Rent (set at least one)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            ParkingTextField(value = uiState.rentPerHour, onValueChange = { viewModel.updateRentPerHour(it) }, label = "Per hour (Rs.)", placeholder = "0 or empty", enabled = !uiState.isLoading)
            Spacer(Modifier.height(12.dp))
            ParkingTextField(value = uiState.rentPerDay, onValueChange = { viewModel.updateRentPerDay(it) }, label = "Per day (Rs.)", placeholder = "0 or empty", enabled = !uiState.isLoading)
            Spacer(Modifier.height(12.dp))
            ParkingTextField(value = uiState.rentMonthly, onValueChange = { viewModel.updateRentMonthly(it) }, label = "Monthly (Rs.)", placeholder = "0 or empty", enabled = !uiState.isLoading)
            Spacer(Modifier.height(24.dp))
            Text("Photos (up to $MAX_IMAGES)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.imageItems.forEachIndexed { index, urlOrPath ->
                    Card(
                        Modifier.size(96.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = if (urlOrPath.startsWith("http")) urlOrPath else File(urlOrPath),
                                contentDescription = null,
                                Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removeImage(index) },
                                Modifier.align(Alignment.TopEnd).size(32.dp)
                            ) {
                                Text("×", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                if (uiState.imageItems.size < MAX_IMAGES) {
                    OutlinedButton(
                        onClick = { showUploadDialog = true },
                        Modifier.height(48.dp),
                        enabled = !uiState.isLoading,
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
                    title = { Text("Add photo") },
                    text = {
                        Column(Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showUploadDialog = false; galleryLauncher.launch("image/*") }) {
                                Text("From gallery", color = MaterialTheme.colorScheme.onSurface)
                            }
                            TextButton(onClick = {
                                showUploadDialog = false
                                if (uiState.imageItems.size < MAX_IMAGES) permissionLauncher.launch(Manifest.permission.CAMERA)
                            }) {
                                Text("Take photo", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    },
                    dismissButton = { TextButton(onClick = { showUploadDialog = false }) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                )
            }
            if (uiState.errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(24.dp))
            ParkingPrimaryButton(text = "Save changes", onClick = { viewModel.submit() }, enabled = !uiState.isLoading, loading = uiState.isLoading)
        }
    }
}
