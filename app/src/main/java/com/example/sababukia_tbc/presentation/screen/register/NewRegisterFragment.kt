package com.example.sababukia_tbc.presentation.screen.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewRegisterBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.screen.login.NewLoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewRegisterFragment :
    BaseFragment<FragmentNewRegisterBinding>(FragmentNewRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val repeatPassword = etRepeatPassword.text.toString()

                if (password != repeatPassword) {
                    Toast.makeText(
                        requireContext(),
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.onEvent(RegisterEvent.Register(email, password))
            }

            btnBack.setOnClickListener {
                viewModel.onEvent(RegisterEvent.OnBackPressed)
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
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
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
                                sideEffect.errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleLoader(resource: Resource<String>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = if (resource.isLoading) View.VISIBLE else View.GONE
                binding.btnRegister.isEnabled = !resource.isLoading
                binding.btnBack.isEnabled = !resource.isLoading
                binding.tvError.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                binding.btnBack.isEnabled = true
                binding.tvError.visibility = View.GONE
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                binding.btnBack.isEnabled = true
                binding.tvError.text = resource.errorMessage
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}
