package com.example.sababukia_tbc

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.example.sababukia_tbc.presentation.common.showNetworkConnectedSnackbar
import com.example.sababukia_tbc.presentation.common.showNetworkDisconnectedSnackbar
import com.example.sababukia_tbc.presentation.screen.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var networkSnackbar: Snackbar? = null

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is MainViewModel.SideEffect.ShowConnectedMessage -> {
                            networkSnackbar?.dismiss()
                            networkSnackbar = binding.root.showNetworkConnectedSnackbar()
                        }
                        is MainViewModel.SideEffect.ShowDisconnectedMessage -> {
                            networkSnackbar?.dismiss()
                            networkSnackbar = binding.root.showNetworkDisconnectedSnackbar()
                        }
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() = with(binding) {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_heart -> {
                    true
                }
                R.id.nav_home -> {
                    true
                }
                R.id.nav_message -> {
                    true
                }
                R.id.nav_notifications -> {
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.nav_home
    }
}