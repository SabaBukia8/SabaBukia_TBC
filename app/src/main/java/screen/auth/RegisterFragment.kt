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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeUiState()
    }

    private fun setupViews() = with(binding) {
        etUsername.doAfterTextChanged { text ->
            viewModel.updateUsername(text?.toString() ?: "")
        }

        etEmail.doAfterTextChanged { text ->
            viewModel.updateEmail(text?.toString() ?: "")
        }

        etPassword.doAfterTextChanged { text ->
            viewModel.updatePassword(text?.toString() ?: "")
        }

        btnRegister.setOnClickListener {
            viewModel.register()
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
                    viewModel.usernameText.collect { username ->
                        if (binding.etUsername.text?.toString() != username) {
                            binding.etUsername.setText(username)
                        }
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
        btnRegister.text = if (state.isLoading) getString(R.string.loading) else getString(R.string.sign_up)
        btnRegister.isEnabled = !state.isLoading

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
            // Navigate to login and prefill username
            navigateToLoginPrefilled(viewModel.usernameText.value)
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

    private fun navigateToLoginPrefilled(username: String) {
        viewModel.resetForm()
        try {
            val action = R.id.action_registerFragment_to_loginFragment
            // We don't have SafeArgs arg defined; set shared state in ViewModel instead
            viewModel.setUsername(username)
            findNavController().navigate(action)
        } catch (e: Exception) {
            showError(getString(R.string.error_navigation, e.message ?: "Unknown error"))
        }
    }
}
