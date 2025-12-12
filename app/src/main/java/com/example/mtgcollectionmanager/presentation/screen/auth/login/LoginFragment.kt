package com.example.mtgcollectionmanager.presentation.screen.auth.login

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mtgcollectionmanager.databinding.FragmentLoginBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.common.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {
    private val viewModel: LoginViewModel by viewModels()

    override fun bind() {
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            etEmail.doAfterTextChanged { text ->
                viewModel.onEvent(LoginContract.Event.EmailChanged(text.toString()))
            }

            etPassword.doAfterTextChanged { text ->
                viewModel.onEvent(LoginContract.Event.PasswordChanged(text.toString()))
            }

            btnLogin.setOnClickListener {
                viewModel.onEvent(LoginContract.Event.LoginClicked)
            }

            tvRegisterLink.setOnClickListener {
                viewModel.onEvent(LoginContract.Event.RegisterClicked)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        if (state.isLoading) {
                            progressBar.show()
                            btnLogin.isEnabled = false
                        } else {
                            progressBar.hide()
                            btnLogin.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is LoginContract.SideEffect.NavigateToRegister -> {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                            )
                        }
                        is LoginContract.SideEffect.NavigateToCollection -> {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToCollectionsListFragment()
                            )
                        }
                        is LoginContract.SideEffect.ShowError -> {
                            binding.root.showErrorSnackbar(sideEffect.message.asString(requireContext()))
                        }
                    }
                }
            }
        }
    }
}
