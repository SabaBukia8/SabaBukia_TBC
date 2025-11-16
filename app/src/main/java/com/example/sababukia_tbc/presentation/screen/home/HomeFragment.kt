package com.example.sababukia_tbc.presentation.screen.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.ui.state.HomeUiState
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private var navigationJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeUiState()
        observeNavigationEvents()
    }

    override fun onDestroyView() {
        navigationJob?.cancel()
        navigationJob = null
        super.onDestroyView()
    }

    private fun setupViews() = with(binding) {
        btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun observeNavigationEvents() {
        navigationJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { event ->
                    when (event) {
                        is NavigationEvent.NavigateToWelcome -> {
                            navigateToWelcome()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateUi(state: HomeUiState) = with(binding) {
        progressBar.isVisible = state.isLoading

        if (state.username != null) {
            tvWelcomeMessage.text = getString(R.string.welcome_user, state.username)
            tvWelcomeMessage.isVisible = true
        } else {
            tvWelcomeMessage.isVisible = false
        }

        if (state.email != null) {
            tvEmail.text = getString(R.string.email_format, state.email)
            tvEmail.isVisible = true
        } else {
            tvEmail.isVisible = false
        }

        if (state.userId != null) {
            tvUserId.text = getString(R.string.user_id_format, state.userId)
            tvUserId.isVisible = true
        } else {
            tvUserId.isVisible = false
        }

        btnLogout.isEnabled = !state.isLoading
    }

    private fun navigateToWelcome() {
        try {
            findNavController().navigate(R.id.action_homeFragment_to_welcomeFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
