package com.example.sababukia_tbc

import android.view.LayoutInflater
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.example.sababukia_tbc.presentation.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun setupUI() {
        // Set up Navigation Component with Bottom Navigation
        val navHostFragment = binding.navHostFragment.getFragment<NavHostFragment>()
        val navController = navHostFragment.navController

        // Connect the bottom navigation view with the navigation controller
        binding.bottomNavigation.setupWithNavController(navController)
    }
}