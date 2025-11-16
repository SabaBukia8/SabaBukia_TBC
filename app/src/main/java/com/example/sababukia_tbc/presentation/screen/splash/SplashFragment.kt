package com.example.sababukia_tbc.presentation.screen.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation()
    }


    private fun observeNavigation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Splash screen delay - only runs when UI is in STARTED state
                delay(2000) // 2 seconds

                viewModel.uiState.collect { uiState ->
                    if (!uiState.isLoading) {
                        when {
                            uiState.isAuthenticated == true -> navigateToHome()
                            uiState.isAuthenticated == false -> navigateToWelcome()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } catch (e: Exception) {
            navigateToWelcome()
        }
    }

    private fun navigateToWelcome() {
        try {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
