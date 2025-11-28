package com.example.sababukia_tbc.presentation.screen.userprofile

import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.databinding.FragmentUserProfileBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(FragmentUserProfileBinding::inflate) {

    private val viewModel: UserProfileViewModel by viewModels()
    private val profileAdapter by lazy {
        UserProfileAdapter { profileId ->
            viewModel.onEvent(UserProfileEvent.OnDeleteProfile(profileId))
        }
    }

    override fun bind() {
        setupRecyclerView()
        setupTextWatchers()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        binding.apply {
            btnSave.setOnClickListener {
                viewModel.onEvent(UserProfileEvent.OnSaveClicked)
            }

            btnRead.setOnClickListener {
                viewModel.onEvent(UserProfileEvent.OnReadClicked)
            }

            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvProfiles.adapter = profileAdapter
    }

    private fun setupTextWatchers() {
        binding.apply {
            etFirstName.addTextChangedListener { text ->
                viewModel.onEvent(UserProfileEvent.OnFirstNameChanged(text.toString()))
            }

            etLastName.addTextChangedListener { text ->
                viewModel.onEvent(UserProfileEvent.OnLastNameChanged(text.toString()))
            }

            etEmail.addTextChangedListener { text ->
                viewModel.onEvent(UserProfileEvent.OnEmailChanged(text.toString()))
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: UserProfileState) {
        binding.apply {
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            if (etFirstName.text.toString() != state.firstName && !etFirstName.isFocused) {
                etFirstName.setText(state.firstName)
            }
            if (etLastName.text.toString() != state.lastName && !etLastName.isFocused) {
                etLastName.setText(state.lastName)
            }
            if (etEmail.text.toString() != state.email && !etEmail.isFocused) {
                etEmail.setText(state.email)
            }

            tilFirstName.error = state.firstNameError
            tilLastName.error = state.lastNameError
            tilEmail.error = state.emailError

            profileAdapter.submitList(state.savedProfiles)

            if (state.savedProfiles.isEmpty()) {
                rvProfiles.visibility = View.GONE
                tvEmptyState.visibility = View.VISIBLE
            } else {
                rvProfiles.visibility = View.VISIBLE
                tvEmptyState.visibility = View.GONE
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is UserProfileSideEffect.ShowMessage -> {
                            Toast.makeText(requireContext(), sideEffect.message, Toast.LENGTH_SHORT).show()
                        }
                        is UserProfileSideEffect.ShowError -> {
                            Toast.makeText(requireContext(), sideEffect.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
