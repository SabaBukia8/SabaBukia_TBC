package com.example.sababukia_tbc.presentation.screen.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentNewHomeBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewHomeFragment : BaseFragment<FragmentNewHomeBinding>(FragmentNewHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeSideEffects()
    }

    override fun listeners() {
        binding.apply {
            btnProfile.setOnClickListener {
                viewModel.onEvent(HomeEvent.OnProfileClicked)
            }
            btnViewUsers.setOnClickListener {
                findNavController().navigate(
                    R.id.action_newHomeFragment_to_usersFragment
                )
            }
            btnUserProfile.setOnClickListener {
                viewModel.onEvent(HomeEvent.OnUserProfileClicked)
            }
        }
    }

    private fun setupListeners() {
        listeners()
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
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
    }
}
