package com.example.client.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun Register(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {

    var email by rememberSaveable{ mutableStateOf("") }
    var pasword by rememberSaveable {mutableStateOf("") }

    Box() {
        Text("Register")
        TextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Enter You email ")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth()

        )

        TextField(
            value = pasword,
            onValueChange = {pasword = it},
            label = {Text("Enter You pasword ")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth()

        )


        TextButton(
            onClick = onLoginClick,
        ) {
            Text("Alredy have account login")
        }

        TextButton(
            onClick = onRegisterClick,
        ) {
            Text("Register")

        }
    }


}

@Preview
@Composable
fun RegisterPre() {
    Register()
}