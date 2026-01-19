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
import com.example.sababukia_tbc.presentation.store.StoreScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        composable<Home> {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Login)
                },
                onNavigateToRegister = {
                    navController.navigate(Register)
                },
                onNavigateToStore = {
                    navController.navigate(Store)
                }
            )
        }

        composable<Login> {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Store) {
                        popUpTo<Home> { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(RegisterNickname) {
                        popUpTo<Register> { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<RegisterNickname> {
            RegisterNicknameScreen(
                onComplete = {
                    navController.navigate(Home) {
                        popUpTo<Home> { inclusive = true }
                    }
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<Store> {
            StoreScreen(
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<Home> { inclusive = true }
                    }
                }
            )
        }
    }
}
