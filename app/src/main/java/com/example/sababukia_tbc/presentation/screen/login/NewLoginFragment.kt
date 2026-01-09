package com.example.sababukia_tbc.presentation.screen.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewLoginBinding
import com.example.sababukia_tbc.presentation.base.BaseFragment
import com.example.sababukia_tbc.presentation.extension.disable
import com.example.sababukia_tbc.presentation.extension.enable
import com.example.sababukia_tbc.presentation.extension.hasNotificationPermission
import com.example.sababukia_tbc.presentation.extension.hide
import com.example.sababukia_tbc.presentation.extension.onClick
import com.example.sababukia_tbc.presentation.extension.requestNotificationPermission
import com.example.sababukia_tbc.presentation.extension.show
import com.example.sababukia_tbc.presentation.extension.toUserMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewLoginFragment : BaseFragment<FragmentNewLoginBinding>(FragmentNewLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("NewLoginFragment", "Notification permission granted")
            Toast.makeText(
                requireContext(),
                "Notifications enabled",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Log.d("NewLoginFragment", "Notification permission denied")
        }
    }

    companion object {
        const val REGISTRATION_KEY = "registration_result"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
    }

    override fun setupViews() {
        setFragmentResultListener(REGISTRATION_KEY) { _, bundle ->
            val email = bundle.getString(EMAIL_KEY) ?: ""
            val password = bundle.getString(PASSWORD_KEY) ?: ""
            binding.etEmail.setText(email)
            binding.etPassword.setText(password)
            Toast.makeText(requireContext(), getString(R.string.message_registration_successful_toast), Toast.LENGTH_SHORT).show()
        }

        // Request notification permission
        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (!hasNotificationPermission()) {
            requestNotificationPermission(notificationPermissionLauncher)
        }
    }

    override fun setupListeners() {
        binding.apply {
            cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onEvent(LoginEvent.RememberMeChanged(isChecked))
            }

            btnLogin.onClick {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.onEvent(LoginEvent.Login(email, password))
            }

            btnRegister.onClick {
                viewModel.onEvent(LoginEvent.OnRegister)
            }
        }
    }

    override fun observeState() {
        collectStateFlow(viewModel.state) { state ->
            handleLoader(state.loader)
            binding.cbRememberMe.isChecked = state.rememberMe
            binding.tilEmail.error = state.emailError
            binding.tilPassword.error = state.passwordError
        }

        collectFlow(viewModel.sideEffect) { sideEffect ->
            when (sideEffect) {
                is LoginSideEffect.NavigateToHome -> {
                    findNavController().navigate(
                        R.id.action_newLoginFragment_to_newHomeFragment
                    )
                }
                is LoginSideEffect.NavigateToPendingDeepLink -> {
                    Log.d("NewLoginFragment", "Navigating to pending deep link: ${sideEffect.uri}")

                    // Create Intent with deep link URI
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        android.net.Uri.parse(sideEffect.uri)
                    ).apply {
                        setPackage(requireContext().packageName)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }

                    // Let MainActivity's NavController handle the deep link
                    requireActivity().startActivity(intent)
                    requireActivity().finish() // Finish current activity to avoid back stack issues
                }
                is LoginSideEffect.NavigateToRegister -> {
                    findNavController().navigate(
                        R.id.action_newLoginFragment_to_newRegisterFragment
                    )
                }
                is LoginSideEffect.ShowError -> {
                    Toast.makeText(
                        requireContext(),
                        sideEffect.error.toUserMessage(requireContext()),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun handleLoader(resource: Resource<String>) {
        binding.apply {
            when (resource) {
                is Resource.Loading -> {
                    if (resource.isLoading) {
                        progressBar.show()
                        btnLogin.disable()
                        btnRegister.disable()
                        tvError.hide()
                    } else {
                        progressBar.hide()
                        btnLogin.enable()
                        btnRegister.enable()
                    }
                }
                is Resource.Success -> {
                    progressBar.hide()
                    btnLogin.enable()
                    btnRegister.enable()
                    tvError.hide()
                }
                is Resource.Error -> {
                    progressBar.hide()
                    btnLogin.enable()
                    btnRegister.enable()
                    tvError.text = resource.error.toUserMessage(requireContext())
                    tvError.show()
                }
            }
        }
    }
}
