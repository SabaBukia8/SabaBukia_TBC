package com.example.sababukia_tbc.presentation.screen.users

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.databinding.FragmentUsersBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : BaseFragment<FragmentUsersBinding>(
    FragmentUsersBinding::inflate
) {
    private val viewModel: UsersViewModel by viewModels()

    private val usersAdapter by lazy {
        UsersPagingAdapter(::onUserClick)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeUsers()
        observeSideEffects()
    }

    private fun initViews() {
        binding.rvUsers.adapter = usersAdapter
    }

    private fun onUserClick(userId: Int) {
        viewModel.onEvent(UsersEvent.OnUserClick(userId))
    }

    private fun observeUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { pagingData ->
                    usersAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is UsersSideEffect.ShowUserDetails -> {
                            Toast.makeText(
                                requireContext(),
                                "User ${sideEffect.userId} clicked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
