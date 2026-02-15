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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.BookingResponse
import com.example.client.data.model.SpaceResponse
import com.example.client.ui.components.SlotCalendar
import com.example.client.viewModels.BookSlotViewModel
import com.example.client.viewModels.SimpleDate

private fun monthName(month: Int): String {
    val names = listOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")
    return names.getOrElse(month - 1) { "" }
}

private fun formatSelectedDate(d: SimpleDate): String {
    val shortMonths = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val m = shortMonths.getOrElse(d.month - 1) { "" }
    return "$m ${d.day}, ${d.year}"
}

@Composable
private fun CheckoutSection(
    booking: BookingResponse,
    paymentLoading: Boolean,
    onPayClick: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

    if (booking.paymentStatus == "PAID") {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Payment successful",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Rs. ${booking.totalAmount} has been paid for your booking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Order summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Booking amount", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Rs. ${booking.totalAmount}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Rs. ${booking.totalAmount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Payment method",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 19) cardNumber = it.filter { c -> c.isDigit() }.chunked(4).joinToString(" ") },
                    label = { Text("Card number") },
                    placeholder = { Text("4242 4242 4242 4242") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = {
                            val digits = it.filter { c -> c.isDigit() }.take(4)
                            expiry = when (digits.length) {
                                0 -> ""
                                1, 2 -> digits
                                else -> "${digits.take(2)}/${digits.drop(2)}"
                            }
                        },
                        label = { Text("Expiry") },
                        placeholder = { Text("MM/YY") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 4) cvv = it.filter { c -> c.isDigit() } },
                        label = { Text("CVV") },
                        placeholder = { Text("123") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Name on card") },
                    placeholder = { Text("John Doe") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Button(
            onClick = onPayClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !paymentLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (paymentLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text("Pay Rs. ${booking.totalAmount}")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Secure payment", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
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

            if (uiState.selectedStartDate != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Selected dates",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "From: ${formatSelectedDate(uiState.selectedStartDate!!)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uiState.selectedEndDate != null) {
                            Text(
                                text = "To: ${formatSelectedDate(uiState.selectedEndDate!!)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = "To: Same day (tap another date for range)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Text(
                text = "Time (12-hour)",
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
                    label = { Text("Start (e.g. 9:00 AM)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.endTime,
                    onValueChange = { viewModel.setEndTime(it) },
                    label = { Text("End (e.g. 5:00 PM)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (uiState.booking != null) {
                CheckoutSection(
                    booking = uiState.booking!!,
                    paymentLoading = uiState.paymentLoading,
                    onPayClick = { viewModel.payDummy() }
                )
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
