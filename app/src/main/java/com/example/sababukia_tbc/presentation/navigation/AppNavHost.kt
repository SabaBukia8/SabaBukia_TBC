package com.example.sababukia_tbc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sababukia_tbc.presentation.screen.registration.RegistrationScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Registration,
        modifier = modifier
    ) {
        composable<Routes.Registration> {
            RegistrationScreen()
        }
    }
}
