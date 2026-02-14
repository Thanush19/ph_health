package com.example.client.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.client.data.model.SpaceResponse
import com.example.client.viewModels.FindParkingViewModel

@Composable
fun FindParking(
    onNavigateBack: () -> Unit = {},
    onSpaceClick: (SpaceResponse) -> Unit = {},
    viewModel: FindParkingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        TopBar(title = "Find parking", onBackClick = onNavigateBack)

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            uiState.spaces.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No spaces listed yet. Be the first to list your land.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.spaces, key = { it.id }) { space ->
                        ParkingSpaceCard(
                            space = space,
                            onClick = { onSpaceClick(space) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Composable
private fun ParkingSpaceCard(
    space: SpaceResponse,
    onClick: () -> Unit = {}
) {
    val firstImageUrl = space.imageUrls?.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (firstImageUrl != null) {
                AsyncImage(
                    model = firstImageUrl,
                    contentDescription = "Space photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                text = space.address,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${space.squareFeet} sq ft · ${space.vehicleTypes}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val rentParts = buildList {
                space.rentPerHour?.takeIf { it > 0 }?.let { add("Rs. $it/hr") }
                space.rentPerDay?.takeIf { it > 0 }?.let { add("Rs. $it/day") }
                space.rentMonthly?.takeIf { it > 0 }?.let { add("Rs. $it/mo") }
            }
            if (rentParts.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = rentParts.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
