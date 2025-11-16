package com.example.sababukia_tbc.presentation.screen.register

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentNewRegisterBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.screen.login.NewLoginFragment
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewRegisterFragment :
    BaseFragment<FragmentNewRegisterBinding>(FragmentNewRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()
    private var navigationJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeUiState()
        observeNavigationEvents()
    }

    override fun listeners() {
        with(binding) {
            etEmail.addTextChangedListener {
                viewModel.onEmailChanged(it.toString())
            }

            etPassword.addTextChangedListener {
                viewModel.onPasswordChanged(it.toString())
            }

            etRepeatPassword.addTextChangedListener {
                viewModel.onRepeatPasswordChanged(it.toString())
            }

            btnRegister.setOnClickListener {
                viewModel.onRegisterClicked()
            }

            btnBack.setOnClickListener {
                viewModel.onBackClicked()
            }
        }
    }

    private fun setupListeners() {
        listeners()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    with(binding) {
                        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        btnRegister.isEnabled = state.isRegisterButtonEnabled && !state.isLoading

                        if (state.isRegisterButtonEnabled && !state.isLoading) {
                            btnRegister.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.primary_color)
                            )
                        } else {
                            btnRegister.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.text_secondary)
                            )
                        }

                        if (state.errorMessage != null) {
                            tvError.text = state.errorMessage
                            tvError.visibility = View.VISIBLE
                        } else {
                            tvError.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun observeNavigationEvents() {
        navigationJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { event ->
                    when (event) {
                        is NavigationEvent.NavigateBackToLoginWithCredentials -> {
                            setFragmentResult(
                                NewLoginFragment.REGISTRATION_KEY,
                                bundleOf(
                                    NewLoginFragment.EMAIL_KEY to event.email,
                                    NewLoginFragment.PASSWORD_KEY to event.password
                                )
                            )
                            findNavController().popBackStack()
                        }

                        is NavigationEvent.NavigateBack -> {
                            findNavController().popBackStack()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        navigationJob?.cancel()
        navigationJob = null
        super.onDestroyView()
    }
}
