package com.example.sababukia_tbc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sababukia_tbc.presentation.home.HomeScreen
import com.example.sababukia_tbc.presentation.login.LoginScreen
import com.example.sababukia_tbc.presentation.register.RegisterNicknameScreen
import com.example.sababukia_tbc.presentation.register.RegisterScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Home.route,
    onShowSnackbar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Home.route) {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.RegisterNickname.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable(Routes.RegisterNickname.route) {
            RegisterNicknameScreen(
                onComplete = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}