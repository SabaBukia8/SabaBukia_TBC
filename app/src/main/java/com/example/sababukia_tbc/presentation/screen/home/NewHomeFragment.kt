package com.example.sababukia_tbc.presentation.screen.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentNewHomeBinding
import com.example.sababukia_tbc.presentation.base.BaseFragment
import com.example.sababukia_tbc.presentation.extension.onClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewHomeFragment : BaseFragment<FragmentNewHomeBinding>(FragmentNewHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    override fun setupListeners() {
        binding.apply {
            btnProfile.onClick {
                viewModel.onEvent(HomeEvent.OnProfileClicked)
            }

            btnViewUsers.onClick {
                findNavController().navigate(
                    R.id.action_newHomeFragment_to_usersFragment
                )
            }

            btnUserProfile.onClick {
                viewModel.onEvent(HomeEvent.OnUserProfileClicked)
            }
        }
    }

    override fun observeState() {
        collectFlow(viewModel.sideEffect) { sideEffect ->
            when (sideEffect) {
                is HomeSideEffect.NavigateToProfile -> {
                    findNavController().navigate(
                        R.id.action_newHomeFragment_to_newProfileFragment
                    )
                }
                is HomeSideEffect.NavigateToUserProfile -> {
                    findNavController().navigate(
                        R.id.action_newHomeFragment_to_userProfileFragment
                    )
                }
                is HomeSideEffect.ShowError -> {
                }
            }
        }
    }
}
