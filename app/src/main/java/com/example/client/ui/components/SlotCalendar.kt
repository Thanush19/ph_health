package com.example.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client.viewModels.SimpleDate
import java.util.Calendar

private val DayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

@Composable
fun SlotCalendar(
    displayYear: Int,
    displayMonth: Int,
    selectedStartDate: SimpleDate?,
    selectedEndDate: SimpleDate?,
    onDateClick: (year: Int, month: Int, day: Int) -> Unit,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true
) {
    if (displayYear == 0 || displayMonth == 0) return
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, displayYear)
        set(Calendar.MONTH, displayMonth - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstWeekday = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingBlanks = firstWeekday
    val totalCells = leadingBlanks + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = modifier.fillMaxWidth()) {
        if (showTitle) {
            Text(
                text = monthYearLabel(displayMonth, displayYear),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayNames.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val dayOfMonth = when {
                        cellIndex < leadingBlanks -> null
                        cellIndex - leadingBlanks < daysInMonth -> (cellIndex - leadingBlanks + 1)
                        else -> null
                    }
                    DayCell(
                        dayOfMonth = dayOfMonth,
                        year = displayYear,
                        month = displayMonth,
                        selectedStart = selectedStartDate,
                        selectedEnd = selectedEndDate,
                        onClick = { d -> onDateClick(displayYear, displayMonth, d) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayCell(
    dayOfMonth: Int?,
    year: Int,
    month: Int,
    selectedStart: SimpleDate?,
    selectedEnd: SimpleDate?,
    onClick: (day: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isStart = dayOfMonth != null && selectedStart?.let { it.year == year && it.month == month && it.day == dayOfMonth } == true
    val isEnd = dayOfMonth != null && selectedEnd?.let { it.year == year && it.month == month && it.day == dayOfMonth } == true
    val inRange = dayOfMonth != null && selectedStart != null && selectedEnd != null && run {
        val d = SimpleDate(year, month, dayOfMonth)
        val a = d.toIsoDateString()
        val s = selectedStart.toIsoDateString()
        val e = selectedEnd.toIsoDateString()
        a in s..e
    }
    val isSingle = isStart && selectedEnd == null
    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (dayOfMonth != null) Modifier
                    .clip(CircleShape)
                    .then(
                        when {
                            isStart || isEnd || isSingle -> Modifier
                                .background(primary)
                                .border(1.dp, primary, CircleShape)
                            inRange -> Modifier.background(container.copy(alpha = 0.6f))
                            else -> Modifier
                        }
                    )
                    .clickable { onClick(dayOfMonth) }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (dayOfMonth != null) {
            Text(
                text = "$dayOfMonth",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = when {
                    isStart || isEnd || isSingle -> onPrimary
                    inRange -> onSurface
                    else -> onSurface
                }
            )
        }
    }
}

private fun monthYearLabel(month: Int, year: Int): String {
    val names = listOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")
    return "${names.getOrElse(month - 1) { "" }} $year"
}
