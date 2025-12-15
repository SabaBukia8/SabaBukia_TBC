package com.example.sababukia_tbc

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        android.util.Log.d("MainActivity", "onCreate called, starting network observation")
        setupBottomNavigation()
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.sideEffect.collect { sideEffect ->
                    android.util.Log.d("MainActivity", "Received network side effect: $sideEffect")
                    when (sideEffect) {
                        is MainViewModel.SideEffect.ShowConnectedMessage -> {
                            android.util.Log.d("MainActivity", "Showing connected snackbar")
                            networkSnackbar?.dismiss()
                            networkSnackbar = binding.root.showNetworkConnectedSnackbar()
                        }
                        is MainViewModel.SideEffect.ShowDisconnectedMessage -> {
                            android.util.Log.d("MainActivity", "Showing disconnected snackbar")
                            networkSnackbar?.dismiss()
                            networkSnackbar = binding.root.showNetworkDisconnectedSnackbar()
                        }
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() = with(binding) {
        // Safely obtain NavController from NavHostFragment. If fragment isn't ready yet,
        // post to the view's message queue to try again shortly.
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHost?.navController
        if (navController != null) {
            bottomNavigation.setupWithNavController(navController)
        } else {
            bottomNavigation.post {
                try {
                    val nc = findNavController(R.id.nav_host_fragment)
                    bottomNavigation.setupWithNavController(nc)
                } catch (ignored: IllegalStateException) {
                    // If still not available, ignore to avoid crashing; nav will work when fragment attaches
                }
            }
        }
    }

    override fun onDestroy() {
        networkSnackbar?.dismiss()
        networkSnackbar = null
        super.onDestroy()
    }
}