package com.example.sababukia_tbc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sababukia_tbc.presentation.chat.ChatScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Chat,
        modifier = modifier
    ) {
        composable<Chat> {
            ChatScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}
