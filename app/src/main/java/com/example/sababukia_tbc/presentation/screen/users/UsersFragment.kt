package com.example.sababukia_tbc.presentation.screen.users

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.sababukia_tbc.databinding.FragmentUsersBinding
import com.example.sababukia_tbc.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : BaseFragment<FragmentUsersBinding>(
    FragmentUsersBinding::inflate
) {
    private val viewModel: UsersViewModel by viewModels()

    private val usersAdapter by lazy {
        UsersPagingAdapter(::onUserClick)
    }

    override fun setupViews() {
        binding.rvUsers.adapter = usersAdapter
        Log.d("UsersFragment", "RecyclerView adapter set")

        // Add load state listener to debug
        lifecycleScope.launch {
            usersAdapter.loadStateFlow.collectLatest { loadStates ->
                when (val refresh = loadStates.refresh) {
                    is LoadState.Loading -> {
                        Log.d("UsersFragment", "Loading users...")
                    }
                    is LoadState.NotLoading -> {
                        Log.d("UsersFragment", "Users loaded successfully. Item count: ${usersAdapter.itemCount}")
                    }
                    is LoadState.Error -> {
                        Log.e("UsersFragment", "Error loading users: ${refresh.error.message}")
                        Toast.makeText(
                            requireContext(),
                            "Error loading users: ${refresh.error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun observeState() {
        collectFlow(viewModel.users) { pagingData ->
            Log.d("UsersFragment", "Submitting paging data to adapter")
            usersAdapter.submitData(pagingData)
        }

        collectFlow(viewModel.sideEffect) { sideEffect ->
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

    private fun onUserClick(userId: Int) {
        viewModel.onEvent(UsersEvent.OnUserClick(userId))
    }
}
