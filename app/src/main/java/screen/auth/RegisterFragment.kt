package screen.auth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import util.AuthConstants

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeUiState()
        prefillTestCredentials() // Auto-fill for easy testing
    }

    private fun prefillTestCredentials() {
        // Pre-fill with working test credentials for easy testing
        binding.etEmail.setText(AuthConstants.VALID_EMAIL)
        binding.etPassword.setText(AuthConstants.TEST_PASSWORD_2) // Use "pistol" for registration
        viewModel.updateEmail(AuthConstants.VALID_EMAIL)
        viewModel.updatePassword(AuthConstants.TEST_PASSWORD_2)
    }

    private fun setupViews() = with(binding) {
        // Setup text watchers
        etEmail.doAfterTextChanged { text ->
            viewModel.updateEmail(text?.toString() ?: "")
        }

        etPassword.doAfterTextChanged { text ->
            viewModel.updatePassword(text?.toString() ?: "")
        }

        // Setup click listeners
        btnRegister.setOnClickListener {
            viewModel.register()
        }

        tvSignIn.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        updateUiState(state)
                    }
                }

                launch {
                    viewModel.emailText.collect { email ->
                        if (binding.etEmail.text?.toString() != email) {
                            binding.etEmail.setText(email)
                        }
                    }
                }

                launch {
                    viewModel.passwordText.collect { password ->
                        if (binding.etPassword.text?.toString() != password) {
                            binding.etPassword.setText(password)
                        }
                    }
                }
            }
        }
    }

    private fun updateUiState(state: AuthUiState) = with(binding) {
        // Update loading state
        progressBar.isVisible = state.isLoading
        btnRegister.text = if (state.isLoading) "" else getString(R.string.sign_up)
        btnRegister.isEnabled = !state.isLoading

        // Update error messages
        if (state.validationErrors.isNotEmpty()) {
            showError(state.validationErrors.joinToString("\n"))
        } else if (state.errorMessage != null) {
            showError(state.errorMessage)
        } else {
            hideError()
        }

        // Handle success
        if (state.isRegistrationSuccessful) {
            showSuccess(getString(R.string.registration_successful))
            // Navigate back to login or to main screen
            viewModel.clearMessages()
            navigateToLogin()
        }
    }

    private fun showError(message: String) = with(binding) {
        tvError.text = message
        tvError.isVisible = true
    }

    private fun hideError() = with(binding) {
        tvError.isVisible = false
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.success_color, null))
            .show()
    }

    private fun navigateToLogin() {
        viewModel.resetForm()
        try {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        } catch (e: Exception) {
            // Handle navigation error gracefully
            showError("Navigation error: ${e.message}")
        }
    }
}