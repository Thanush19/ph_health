package com.example.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.client.data.local.TokenManager
import com.example.client.screens.ActivitiesScreen
import com.example.client.screens.EditSpace
import com.example.client.screens.FindParking
import com.example.client.screens.IntroScreen
import com.example.client.screens.Login
import com.example.client.screens.MainTabScreen
import com.example.client.screens.MyRentalSpacesScreen
import com.example.client.screens.ParkingDetails
import com.example.client.screens.Register
import com.example.client.screens.RentSpace

sealed class Screen(val route: String) {
    object Intro : Screen("intro")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object RentSpace : Screen("rent_space")
    object FindParking : Screen("find_parking")
    object ParkingDetails : Screen("parking_details")
    object Activities : Screen("activities")
    object MyRentalSpaces : Screen("my_rental_spaces")
    object EditSpace : Screen("edit_space")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }
    val startDestination = if (tokenManager.isLoggedIn()) Screen.Home.route else Screen.Intro.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Intro.route) {
            IntroScreen(
                onGetStartedClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            Login(
                onSignUpClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            Register(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            MainTabScreen(
                navController = navController,
                tokenManager = tokenManager,
                onRentSpaceClick = { navController.navigate(Screen.RentSpace.route) },
                onFindParkingClick = { navController.navigate(Screen.FindParking.route) },
                onNavigateToActivities = { navController.navigate(Screen.Activities.route) },
                onNavigateToMyRentalSpaces = { navController.navigate(Screen.MyRentalSpaces.route) },
                onLogout = {
                    tokenManager.clearToken()
                    navController.navigate(Screen.Intro.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.RentSpace.route) {
            RentSpace(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.FindParking.route) {
            FindParking(
                onNavigateBack = { navController.popBackStack() },
                onSpaceClick = { space ->
                    SelectedSpaceHolder.space = space
                    SelectedSpaceHolder.canEdit = false
                    navController.navigate(Screen.ParkingDetails.route)
                }
            )
        }
        composable(Screen.ParkingDetails.route) {
            ParkingDetails(
                space = SelectedSpaceHolder.space,
                onNavigateBack = { navController.popBackStack() },
                canEdit = SelectedSpaceHolder.canEdit,
                onEditClick = {
                    navController.navigate(Screen.EditSpace.route)
                }
            )
        }
        composable(Screen.Activities.route) {
            ActivitiesScreen(
                onNavigateBack = { navController.popBackStack() },
                onSpaceClick = { space ->
                    SelectedSpaceHolder.space = space
                    SelectedSpaceHolder.canEdit = false
                    navController.navigate(Screen.ParkingDetails.route)
                }
            )
        }
        composable(Screen.MyRentalSpaces.route) {
            MyRentalSpacesScreen(
                onNavigateBack = { navController.popBackStack() },
                onSpaceClick = { space ->
                    SelectedSpaceHolder.space = space
                    SelectedSpaceHolder.canEdit = true
                    navController.navigate(Screen.ParkingDetails.route)
                }
            )
        }
        composable(Screen.EditSpace.route) {
            EditSpace(
                space = SelectedSpaceHolder.space,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
