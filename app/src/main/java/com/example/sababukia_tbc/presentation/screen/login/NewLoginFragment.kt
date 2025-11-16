package com.example.sababukia_tbc.presentation.screen.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentNewLoginBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewLoginFragment : BaseFragment<FragmentNewLoginBinding>(FragmentNewLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()
    private var navigationJob: Job? = null

    companion object {
        const val REGISTRATION_KEY = "registration_result"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentResultListener()
        setupListeners()
        observeUiState()
        observeNavigationEvents()
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(REGISTRATION_KEY) { _, bundle ->
            val email = bundle.getString(EMAIL_KEY) ?: ""
            val password = bundle.getString(PASSWORD_KEY) ?: ""
            viewModel.setCredentialsFromRegistration(email, password)
        }
    }

    override fun listeners() {
        with(binding) {
            etEmail.addTextChangedListener {
                viewModel.onEmailChanged(it.toString())
            }

            etPassword.addTextChangedListener {
                viewModel.onPasswordChanged(it.toString())
            }

            cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onRememberMeChanged(isChecked)
            }

            btnLogin.setOnClickListener {
                viewModel.onLoginClicked()
            }

            btnRegister.setOnClickListener {
                viewModel.onRegisterClicked()
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
                        btnLogin.isEnabled = state.isLoginButtonEnabled && !state.isLoading
                        btnRegister.isEnabled = !state.isLoading

                        // Only set text if different to avoid cursor jumping
                        if (etEmail.text.toString() != state.email) {
                            etEmail.setText(state.email)
                            etEmail.setSelection(state.email.length)
                        }
                        if (etPassword.text.toString() != state.password) {
                            etPassword.setText(state.password)
                            etPassword.setSelection(state.password.length)
                        }
                        cbRememberMe.isChecked = state.rememberMe

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
                        is NavigationEvent.NavigateToHome -> {
                            findNavController().navigate(R.id.action_newLoginFragment_to_newHomeFragment)
                        }
                        is NavigationEvent.NavigateToRegister -> {
                            findNavController().navigate(R.id.action_newLoginFragment_to_newRegisterFragment)
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
