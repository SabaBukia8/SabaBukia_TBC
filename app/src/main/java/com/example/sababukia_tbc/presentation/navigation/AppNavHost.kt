package com.example.sababukia_tbc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.sababukia_tbc.feature.registration.presentation.navigation.RegistrationRoute
import com.example.sababukia_tbc.feature.registration.presentation.navigation.registrationScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = RegistrationRoute,
        modifier = modifier
    ) {
        registrationScreen()
    }
}
