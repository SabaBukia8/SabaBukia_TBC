package com.example.sababukia_tbc.presentation.screen.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewProfileBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.common.hide
import com.example.sababukia_tbc.presentation.common.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewProfileFragment : BaseFragment<FragmentNewProfileBinding>(FragmentNewProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            btnLogout.setOnClickListener {
                viewModel.onEvent(ProfileEvent.OnLogout)
            }

            btnBack.setOnClickListener {
                viewModel.onEvent(ProfileEvent.OnBackPressed)
            }
        }
    }

    private fun setupListeners() {
        listeners()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    handleLoader(state.loader)
                    binding.tvEmail.text = state.userEmail
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is ProfileSideEffect.NavigateToLogin -> {
                            findNavController().navigate(
                                R.id.action_newProfileFragment_to_newLoginFragment
                            )
                        }
                        is ProfileSideEffect.NavigateBack -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun handleLoader(resource: Resource<String>) {
        binding.apply {
            when (resource) {
                is Resource.Loading -> {
                    if (resource.isLoading) progressBar.show() else progressBar.hide()
                    btnLogout.isEnabled = !resource.isLoading
                    btnBack.isEnabled = !resource.isLoading
                }
                is Resource.Success -> {
                    progressBar.hide()
                    btnLogout.isEnabled = true
                    btnBack.isEnabled = true
                }
                is Resource.Error -> {
                    progressBar.hide()
                    btnLogout.isEnabled = true
                    btnBack.isEnabled = true
                }
            }
        }
    }
}
