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
import com.example.sababukia_tbc.presentation.adapter.UsersAdapter
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewHomeFragment : BaseFragment<FragmentNewHomeBinding>(FragmentNewHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private val usersAdapter = UsersAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.rvUsers.adapter = usersAdapter
    }

    override fun listeners() {
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_newHomeFragment_to_newProfileFragment)
        }
    }

    private fun setupListeners() {
        listeners()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    with(binding) {
                        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        
                        if (state.errorMessage != null) {
                            tvError.text = state.errorMessage
                            tvError.visibility = View.VISIBLE
                            rvUsers.visibility = View.GONE
                        } else {
                            tvError.visibility = View.GONE
                            rvUsers.visibility = View.VISIBLE
                            usersAdapter.submitList(state.users)
                        }
                    }
                }
            }
        }
    }
}
