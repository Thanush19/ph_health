package com.example.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.client.data.local.TokenManager
import com.example.client.screens.Home
import com.example.client.screens.IntroScreen
import com.example.client.screens.Login
import com.example.client.screens.Register
import com.example.client.screens.FindParking
import com.example.client.screens.RentSpace

sealed class Screen(val route: String) {
    object Intro : Screen("intro")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object RentSpace : Screen("rent_space")
    object FindParking : Screen("find_parking")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }
    
    val isLoggedIn = remember { tokenManager.isLoggedIn() }
    
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Intro.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Intro.route) {
            IntroScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Login.route) {
            Login(
                onSignUpClick = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            Register(
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            Home(
                onRentSpaceClick = { navController.navigate(Screen.RentSpace.route) },
                onFindParkingClick = { navController.navigate(Screen.FindParking.route) }
            )
        }
        composable(Screen.RentSpace.route) {
            RentSpace(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FindParking.route) {
            FindParking(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

