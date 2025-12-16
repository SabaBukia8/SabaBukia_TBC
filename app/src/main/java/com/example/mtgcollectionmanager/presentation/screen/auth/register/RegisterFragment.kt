package com.example.mtgcollectionmanager.presentation.screen.auth.register

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mtgcollectionmanager.databinding.FragmentRegisterBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.common.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(
    FragmentRegisterBinding::inflate
) {
    private val viewModel: RegisterViewModel by viewModels()

    override fun bind() {
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            etNickname.doAfterTextChanged { text ->
                viewModel.onEvent(RegisterContract.Event.NicknameChanged(text.toString()))
            }

            etEmail.doAfterTextChanged { text ->
                viewModel.onEvent(RegisterContract.Event.EmailChanged(text.toString()))
            }

            etPassword.doAfterTextChanged { text ->
                viewModel.onEvent(RegisterContract.Event.PasswordChanged(text.toString()))
            }

            etConfirmPassword.doAfterTextChanged { text ->
                viewModel.onEvent(RegisterContract.Event.ConfirmPasswordChanged(text.toString()))
            }

            btnRegister.setOnClickListener {
                viewModel.onEvent(RegisterContract.Event.RegisterClicked)
            }

            tvLoginLink.setOnClickListener {
                viewModel.onEvent(RegisterContract.Event.LoginClicked)
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
                            btnRegister.isEnabled = false
                        } else {
                            progressBar.hide()
                            btnRegister.isEnabled = true
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
                        is RegisterContract.SideEffect.NavigateToLogin -> {
                            findNavController().popBackStack()
                        }

                        is RegisterContract.SideEffect.NavigateToCollection -> {
                            findNavController().navigate(
                                RegisterFragmentDirections.actionRegisterFragmentToCollectionsListFragment()
                            )
                        }

                        is RegisterContract.SideEffect.ShowError -> {
                            binding.root.showErrorSnackbar(
                                sideEffect.message.asString(
                                    requireContext()
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
