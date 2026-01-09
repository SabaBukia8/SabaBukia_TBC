package com.example.sababukia_tbc.presentation.screen.register

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewRegisterBinding
import com.example.sababukia_tbc.presentation.base.BaseFragment
import com.example.sababukia_tbc.presentation.extension.disable
import com.example.sababukia_tbc.presentation.extension.enable
import com.example.sababukia_tbc.presentation.extension.hide
import com.example.sababukia_tbc.presentation.extension.onClick
import com.example.sababukia_tbc.presentation.extension.show
import com.example.sababukia_tbc.presentation.extension.toUserMessage
import com.example.sababukia_tbc.presentation.screen.login.NewLoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewRegisterFragment :
    BaseFragment<FragmentNewRegisterBinding>(FragmentNewRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun setupListeners() {
        binding.apply {
            btnRegister.onClick {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val repeatPassword = etRepeatPassword.text.toString()

                viewModel.onEvent(RegisterEvent.Register(email, password, repeatPassword))
            }

            btnBack.onClick {
                viewModel.onEvent(RegisterEvent.OnBackPressed)
            }
        }
    }

    override fun observeState() {
        collectStateFlow(viewModel.state) { state ->
            handleLoader(state.loader)
            binding.tilEmail.error = state.emailError
            binding.tilPassword.error = state.passwordError
            binding.tilRepeatPassword.error = state.repeatPasswordError
        }

        collectFlow(viewModel.sideEffect) { sideEffect ->
            when (sideEffect) {
                is RegisterSideEffect.NavigateBackToLogin -> {
                    setFragmentResult(
                        NewLoginFragment.REGISTRATION_KEY,
                        bundleOf(
                            NewLoginFragment.EMAIL_KEY to sideEffect.email,
                            NewLoginFragment.PASSWORD_KEY to sideEffect.password
                        )
                    )
                    findNavController().popBackStack()
                }

                is RegisterSideEffect.NavigateBack -> {
                    findNavController().popBackStack()
                }

                is RegisterSideEffect.ShowError -> {
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
                        btnRegister.disable()
                        btnBack.disable()
                        tvError.hide()
                    } else {
                        progressBar.hide()
                        btnRegister.enable()
                        btnBack.enable()
                    }
                }
                is Resource.Success -> {
                    progressBar.hide()
                    btnRegister.enable()
                    btnBack.enable()
                    tvError.hide()
                }
                is Resource.Error -> {
                    progressBar.hide()
                    btnRegister.enable()
                    btnBack.enable()
                    tvError.text = resource.error.toUserMessage(requireContext())
                    tvError.show()
                }
            }
        }
    }
}
