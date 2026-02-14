package com.example.client.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.SpaceResponse
import com.example.client.ui.components.SlotCalendar
import com.example.client.viewModels.BookSlotViewModel

private fun monthName(month: Int): String {
    val names = listOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")
    return names.getOrElse(month - 1) { "" }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
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
                .verticalScroll(rememberScrollState())
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

            Text(
                text = "Calendar",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            val cal = remember { Calendar.getInstance() }
            val startYear = remember(cal) { cal.get(Calendar.YEAR) - 2 }
            val initialIndex = remember(cal) {
                val y = cal.get(Calendar.YEAR)
                val m = cal.get(Calendar.MONTH) + 1
                (y - startYear) * 12 + (m - 1)
            }
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex.coerceIn(0, 59))
            val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            val scope = rememberCoroutineScope()
            val visibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
            val visibleYear = startYear + (visibleIndex / 12)
            val visibleMonth = (visibleIndex % 12) + 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    scope.launch {
                        listState.animateScrollToItem((listState.firstVisibleItemIndex - 1).coerceAtLeast(0))
                    }
                }) {
                    Text("<", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    text = "${monthName(visibleMonth)} $visibleYear",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = {
                    scope.launch {
                        listState.animateScrollToItem((listState.firstVisibleItemIndex + 1).coerceAtMost(59))
                    }
                }) {
                    Text(">", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                LazyRow(
                    state = listState,
                    flingBehavior = snapBehavior,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(60, key = { it }) { index ->
                        val year = startYear + (index / 12)
                        val month = (index % 12) + 1
                        SlotCalendar(
                            displayYear = year,
                            displayMonth = month,
                            selectedStartDate = uiState.selectedStartDate,
                            selectedEndDate = uiState.selectedEndDate,
                            onDateClick = { y, m, d -> viewModel.selectDate(y, m, d) },
                            modifier = Modifier.width(maxWidth),
                            showTitle = false
                        )
                    }
                }
            }

            Text(
                text = "Time",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.startTime,
                    onValueChange = { viewModel.setStartTime(it) },
                    label = { Text("Start (e.g. 09:00)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.endTime,
                    onValueChange = { viewModel.setEndTime(it) },
                    label = { Text("End (e.g. 17:00)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

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
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
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
                val canCreate = uiState.selectedStartDate != null && uiState.startTime.isNotBlank()
                Button(
                    onClick = { viewModel.createBooking() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.bookingLoading && canCreate
                ) {
                    if (uiState.bookingLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
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
