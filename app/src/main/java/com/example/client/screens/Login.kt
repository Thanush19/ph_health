package com.example.client.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Login(
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium
            )

            TextField(
                value = email,
                onValueChange = {email = it},
                label = {Text("Enter Your email")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth()

            )

            TextField(
                value = password,
                onValueChange = {password = it},
                label =  {Text(("Enter Your password"))},
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),

            )

            TextButton(
                onClick = onSignUpClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Didnt have  , Sign up")
            }

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Login")

            }


        }

    }
    }

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Login()
}
