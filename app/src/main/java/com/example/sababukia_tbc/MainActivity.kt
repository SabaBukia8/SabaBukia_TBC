package com.example.sababukia_tbc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.sababukia_tbc.presentation.common.LocalSnackbarController
import com.example.sababukia_tbc.presentation.common.SnackbarController
import com.example.sababukia_tbc.presentation.components.BottomNavigationBar
import com.example.sababukia_tbc.presentation.navigation.AppNavHost
import com.example.sababukia_tbc.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(darkTheme = true) {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val snackbarController = remember(snackbarHostState, scope) {
                    SnackbarController(snackbarHostState, scope)
                }

                CompositionLocalProvider(
                    LocalSnackbarController provides snackbarController
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        containerColor = AppTheme.colors.backgroundGradientStart,
                        bottomBar = { BottomNavigationBar() }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(AppTheme.colors.backgroundGradient)
                        ) {
                            AppNavHost()
                        }
                    }
                }
            }
        }
    }
}
