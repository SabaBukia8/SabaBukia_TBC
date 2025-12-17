package com.example.mtgcollectionmanager.presentation.screen.profile

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.databinding.FragmentProfileBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {
    private val viewModel: ProfileViewModel by viewModels()

    override fun bind() {
        observeState()
        observeSideEffects()
    }

    override fun listeners() = with(binding) {
        tilNickname.setEndIconOnClickListener {
            viewModel.onEvent(ProfileContract.Event.ToggleEditNickname)
        }

        etNickname.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onEvent(ProfileContract.Event.SaveNickname(etNickname.text.toString()))
                true
            } else {
                false
            }
        }

        btnChangePassword.setOnClickListener {
            viewModel.onEvent(ProfileContract.Event.ChangePasswordClicked)
        }

        btnLogout.setOnClickListener {
            viewModel.onEvent(ProfileContract.Event.LogoutClicked)
        }

        btnDeleteAccount.setOnClickListener {
            viewModel.onEvent(ProfileContract.Event.DeleteAccountClicked)
        }


    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {

                        networkStatusView.updateNetworkStatus(
                            if (state.isNetworkAvailable)
                                NetworkConnectivityManager.NetworkState.Available
                            else
                                NetworkConnectivityManager.NetworkState.Unavailable
                        )

                        progressBar.isVisible = state.isLoading
                        scrollView.isVisible = !state.isLoading

                        etNickname.setText(state.nickname)
                        etNickname.isEnabled = state.isEditingNickname
                        tilNickname.endIconDrawable = if (state.isEditingNickname) {
                            requireContext().getDrawable(R.drawable.ic_add_outlined)
                        } else {
                            requireContext().getDrawable(R.drawable.ic_edit_outlined)
                        }

                        etEmail.setText(state.email)
                        tvMemberSince.text =
                            getString(R.string.member_since) + ": " + state.memberSince

                        tvCollectionCount.text = "${state.collectionCount} Collections"
                        tvTotalCards.text = "${state.totalCards} Cards"
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
                        is ProfileContract.SideEffect.ShowError -> {
                            showSnackbar(sideEffect.message.asString(requireContext()))
                        }

                        is ProfileContract.SideEffect.ShowSuccess -> {
                            showSnackbar(sideEffect.message.asString(requireContext()))
                        }

                        ProfileContract.SideEffect.ShowChangePasswordDialog -> {
                            showChangePasswordDialog()
                        }

                        ProfileContract.SideEffect.ShowDeleteAccountDialog -> {
                            showDeleteAccountDialog()
                        }

                        ProfileContract.SideEffect.ShowLogoutConfirmation -> {
                            showLogoutConfirmationDialog()
                        }

                        ProfileContract.SideEffect.NavigateToLogin -> {
                            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        dialogView.findViewById<TextInputLayout>(R.id.tilCurrentPassword)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        dialogView.findViewById<TextInputLayout>(R.id.tilNewPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        dialogView.findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_password)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                viewModel.onEvent(
                    ProfileContract.Event.ChangePassword(
                        currentPassword = etCurrentPassword.text.toString(),
                        newPassword = etNewPassword.text.toString(),
                        confirmPassword = etConfirmPassword.text.toString()
                    )
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteAccountDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete_account, null)
        val etPassword = dialogView.findViewById<TextInputEditText>(R.id.etPassword)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_account_confirm)
            .setMessage(R.string.delete_account_message)
            .setView(dialogView)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.onEvent(
                    ProfileContract.Event.ConfirmDeleteAccount(etPassword.text.toString())
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirm)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.menu_logout) { _, _ ->
                viewModel.onEvent(ProfileContract.Event.ConfirmLogout)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
