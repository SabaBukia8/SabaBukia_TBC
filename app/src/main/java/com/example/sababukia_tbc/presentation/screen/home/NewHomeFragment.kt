package com.example.sababukia_tbc.presentation.screen.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.data.common.Resource
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
        observeState()
        observeSideEffects()
    }

    private fun setupRecyclerView() {
        binding.rvUsers.adapter = usersAdapter
    }

    override fun listeners() {
        binding.btnProfile.setOnClickListener {
            viewModel.onEvent(HomeEvent.OnProfileClicked)
        }
    }

    private fun setupListeners() {
        listeners()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    handleLoader(state.loader)
                }
            }
        }
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
                        is HomeSideEffect.ShowError -> {
                            Toast.makeText(
                                requireContext(),
                                sideEffect.errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleLoader(resource: Resource<List<com.example.sababukia_tbc.domain.model.User>>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = if (resource.isLoading) View.VISIBLE else View.GONE
                binding.tvError.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.GONE
                binding.rvUsers.visibility = View.VISIBLE
                usersAdapter.submitList(resource.data)
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.rvUsers.visibility = View.GONE
                binding.tvError.text = resource.errorMessage
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}
