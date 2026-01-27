package com.example.sababukia_tbc.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sababukia_tbc.presentation.statistics.StatisticsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Statistics,
        modifier = modifier
    ) {
        composable<Statistics> {
            StatisticsScreen(snackbarHostState = snackbarHostState)
        }
    }
}
