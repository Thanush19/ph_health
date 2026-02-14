package com.example.client.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.SpaceResponse
import com.example.client.viewModels.BookSlotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSlotScreen(
    space: SpaceResponse?,
    onNavigateBack: () -> Unit,
    viewModel: BookSlotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(space) {
        space?.let { viewModel.setSpace(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        TopAppBar(
            title = { Text("Book my slot", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Text("←", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (space != null) {
                Text(
                    text = space.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedTextField(
                value = uiState.slotStart,
                onValueChange = { viewModel.setSlotStart(it) },
                label = { Text("Slot start (e.g. 2025-02-02T09:00:00)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.slotEnd,
                onValueChange = { viewModel.setSlotEnd(it) },
                label = { Text("Slot end (e.g. 2025-02-02T17:00:00)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.booking != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Booking created", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Amount: Rs. ${uiState.booking!!.totalAmount}", style = MaterialTheme.typography.bodyLarge)
                        Text("Status: ${uiState.booking!!.paymentStatus}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (uiState.booking!!.paymentStatus != "PAID") {
                            Button(
                                onClick = { viewModel.payDummy() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.paymentLoading
                            ) {
                                if (uiState.paymentLoading) {
                                    CircularProgressIndicator(modifier = Modifier.height(20.dp).padding(4.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                } else {
                                    Text("Pay with dummy payment")
                                }
                            }
                        } else {
                            Text("Paid successfully (dummy)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.createBooking() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.bookingLoading && uiState.slotStart.isNotBlank() && uiState.slotEnd.isNotBlank()
                ) {
                    if (uiState.bookingLoading) {
                        CircularProgressIndicator(modifier = Modifier.height(20.dp).padding(4.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text("Create booking")
                    }
                }
            }
            uiState.error?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
