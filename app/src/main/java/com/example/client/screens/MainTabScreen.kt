package com.example.client.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.client.data.local.TokenManager
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
    onRentSpaceClick: () -> Unit,
    onFindParkingClick: () -> Unit,
    onNavigateToActivities: () -> Unit,
    onNavigateToMyRentalSpaces: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Home",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "About me",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
        when (selectedTab) {
            0 -> Home(
                onRentSpaceClick = onRentSpaceClick,
                onFindParkingClick = onFindParkingClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
            1 -> AboutMeScreen(
                onActivitiesClick = onNavigateToActivities,
                onMyRentalSpacesClick = onNavigateToMyRentalSpaces,
                onLogoutClick = {
                    tokenManager.clearToken()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}
