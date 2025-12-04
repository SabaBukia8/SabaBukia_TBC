package com.example.sababukia_tbc.presentation.screen.userlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentUserListBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.util.asString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : BaseFragment<FragmentUserListBinding>(FragmentUserListBinding::inflate) {

    private val viewModel: UserListViewModel by viewModels()
    private val adapter by lazy { UserListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun bind() {
        observeState()
        observeSideEffects()
    }

    private fun setupRecyclerView() {
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@UserListFragment.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    adapter.submitList(state.users)

                    binding.tvOnlineStatus.apply {
                        text = getString(
                            if (state.isOnline) R.string.you_are_online
                            else R.string.you_are_offline
                        )
                        setBackgroundColor(
                            if (state.isOnline) {
                                ContextCompat.getColor(requireContext(), R.color.status_online)
                            } else {
                                ContextCompat.getColor(requireContext(), R.color.red)
                            }
                        )
                    }

                    if (state.isLoadingFromServer) {
                        binding.progressBar.show()
                    } else {
                        binding.progressBar.hide()
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
                        is UserListSideEffect.ShowError -> {
                            Toast.makeText(
                                requireContext(),
                                sideEffect.message.asString(this@UserListFragment),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
