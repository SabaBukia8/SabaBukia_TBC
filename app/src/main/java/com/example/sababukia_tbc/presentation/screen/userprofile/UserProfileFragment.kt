package com.example.sababukia_tbc.presentation.screen.userprofile

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.sababukia_tbc.databinding.FragmentUserProfileBinding
import com.example.sababukia_tbc.presentation.base.BaseFragment
import com.example.sababukia_tbc.presentation.extension.hide
import com.example.sababukia_tbc.presentation.extension.onClick
import com.example.sababukia_tbc.presentation.extension.onTextChanged
import com.example.sababukia_tbc.presentation.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(FragmentUserProfileBinding::inflate) {

    private val viewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()

    private val profileAdapter by lazy {
        UserProfileAdapter { profileId ->
            viewModel.onEvent(UserProfileEvent.OnDeleteProfile(profileId))
        }
    }

    override fun setupViews() {
        binding.rvProfiles.adapter = profileAdapter

        // Handle deep link userId if provided
        val userId = args.userId
        Log.d("UserProfileFragment", "Received userId from deep link: $userId")
        viewModel.onEvent(UserProfileEvent.LoadUser(userId))
    }

    override fun setupListeners() {
        binding.apply {
            btnSave.onClick {
                viewModel.onEvent(UserProfileEvent.OnSaveClicked)
            }

            btnRead.onClick {
                viewModel.onEvent(UserProfileEvent.OnReadClicked)
            }

            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            etFirstName.onTextChanged { text ->
                viewModel.onEvent(UserProfileEvent.OnFirstNameChanged(text))
            }

            etLastName.onTextChanged { text ->
                viewModel.onEvent(UserProfileEvent.OnLastNameChanged(text))
            }

            etEmail.onTextChanged { text ->
                viewModel.onEvent(UserProfileEvent.OnEmailChanged(text))
            }
        }
    }

    override fun observeState() {
        collectStateFlow(viewModel.state) { state ->
            binding.apply {
                if (state.isLoading) {
                    progressBar.show()
                } else {
                    progressBar.hide()
                }

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
                    rvProfiles.hide()
                    tvEmptyState.show()
                } else {
                    rvProfiles.show()
                    tvEmptyState.hide()
                }
            }
        }

        collectFlow(viewModel.sideEffect) { sideEffect ->
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
