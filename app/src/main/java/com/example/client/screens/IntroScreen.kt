package com.example.client.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.client.R

@Composable
fun IntroScreen(
    onGetStartedClick: () -> Unit = {}
) {
    Box() {
        Image(
            painter = painterResource(id =  R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop
            )

        Text(text = "Welcome to ph health checker")

        TextButton(onClick = onGetStartedClick) { Text("Get Started") }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    IntroScreen()
}
