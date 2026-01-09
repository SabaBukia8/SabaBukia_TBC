package com.example.sababukia_tbc.presentation.screen.profile

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewProfileBinding
import com.example.sababukia_tbc.presentation.base.BaseFragment
import com.example.sababukia_tbc.presentation.extension.disable
import com.example.sababukia_tbc.presentation.extension.enable
import com.example.sababukia_tbc.presentation.extension.hide
import com.example.sababukia_tbc.presentation.extension.onClick
import com.example.sababukia_tbc.presentation.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewProfileFragment : BaseFragment<FragmentNewProfileBinding>(FragmentNewProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun setupListeners() {
        binding.apply {
            btnLogout.onClick {
                viewModel.onEvent(ProfileEvent.OnLogout)
            }

            btnBack.onClick {
                viewModel.onEvent(ProfileEvent.OnBackPressed)
            }
        }
    }

    override fun observeState() {
        collectStateFlow(viewModel.state) { state ->
            binding.tvEmail.text = state.userEmail
            handleLoader(state.loader)
        }

        collectFlow(viewModel.sideEffect) { sideEffect ->
            when (sideEffect) {
                is ProfileSideEffect.NavigateToLogin -> {
                    findNavController().navigate(
                        R.id.action_newProfileFragment_to_newLoginFragment
                    )
                }
                is ProfileSideEffect.NavigateBack -> {
                    findNavController().popBackStack()
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
                        btnLogout.disable()
                        btnBack.disable()
                    } else {
                        progressBar.hide()
                        btnLogout.enable()
                        btnBack.enable()
                    }
                }
                is Resource.Success -> {
                    progressBar.hide()
                    btnLogout.enable()
                    btnBack.enable()
                }
                is Resource.Error -> {
                    progressBar.hide()
                    btnLogout.enable()
                    btnBack.enable()
                }
            }
        }
    }
}
