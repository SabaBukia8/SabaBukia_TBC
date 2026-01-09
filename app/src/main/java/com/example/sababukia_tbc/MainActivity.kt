package com.example.sababukia_tbc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.example.sababukia_tbc.domain.usecase.CheckSessionUseCase
import com.example.sababukia_tbc.domain.usecase.SavePendingDeepLinkUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var checkSessionUseCase: CheckSessionUseCase

    @Inject
    lateinit var savePendingDeepLinkUseCase: SavePendingDeepLinkUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        handleDeepLinkWithAuth(intent)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLinkWithAuth(intent)
    }

    private fun handleDeepLinkWithAuth(intent: Intent?) {
        intent?.data?.let { uri ->
            Log.d("MainActivity", "Deep link received: $uri")

            lifecycleScope.launch {
                val isLoggedIn = checkSessionUseCase()

                if (isLoggedIn) {
                    Log.d("MainActivity", "User logged in, navigating to: $uri")
                    if (!navController.handleDeepLink(intent)) {
                        Log.e("MainActivity", "Failed to handle deep link: $uri")
                    }
                } else {
                    Log.d("MainActivity", "User not logged in, saving deep link: $uri")
                    savePendingDeepLinkUseCase(uri.toString())
                }
            }
        }
    }
}