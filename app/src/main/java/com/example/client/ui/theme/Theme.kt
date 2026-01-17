package com.example.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.client.ui.theme.Black
import com.example.client.ui.theme.White

private val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = Black,
    tertiary = Black,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
    outline = Black,
    outlineVariant = Black.copy(alpha = 0.3f)
)

@Composable
fun ClientTheme(
    darkTheme: Boolean = false, // Always use light theme for black and white
    dynamicColor: Boolean = false, // Disable dynamic colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}