package com.example.sababukia_tbc.presentation.screen.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewLoginBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewLoginFragment : BaseFragment<FragmentNewLoginBinding>(FragmentNewLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    companion object {
        const val REGISTRATION_KEY = "registration_result"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentResultListener()
        setupListeners()
        observeState()
        observeSideEffects()
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(REGISTRATION_KEY) { _, bundle ->
            val email = bundle.getString(EMAIL_KEY) ?: ""
            val password = bundle.getString(PASSWORD_KEY) ?: ""
            binding.etEmail.setText(email)
            binding.etPassword.setText(password)
            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun listeners() {
        with(binding) {
            cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateRememberMe(isChecked)
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.onEvent(LoginEvent.Login(email, password))
            }

            btnRegister.setOnClickListener {
                viewModel.onEvent(LoginEvent.OnRegister)
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
                    binding.cbRememberMe.isChecked = state.rememberMe
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is LoginSideEffect.NavigateToHome -> {
                            findNavController().navigate(
                                R.id.action_newLoginFragment_to_newHomeFragment
                            )
                        }
                        is LoginSideEffect.NavigateToRegister -> {
                            findNavController().navigate(
                                R.id.action_newLoginFragment_to_newRegisterFragment
                            )
                        }
                        is LoginSideEffect.ShowError -> {
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
                binding.btnLogin.isEnabled = !resource.isLoading
                binding.btnRegister.isEnabled = !resource.isLoading
                binding.tvError.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                binding.btnRegister.isEnabled = true
                binding.tvError.visibility = View.GONE
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                binding.btnRegister.isEnabled = true
                binding.tvError.text = resource.errorMessage
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}
