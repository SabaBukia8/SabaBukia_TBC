package com.example.sababukia_tbc.presentation.screen.statistics

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.databinding.FragmentStatisticsBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.util.asString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>(FragmentStatisticsBinding::inflate) {

    private val viewModel: StatisticsViewModel by viewModels()
    private val adapter by lazy { WorkspaceAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupBottomNavigation()
    }

    override fun bind() {
        observeState()
        observeSideEffects()
    }

    private fun setupViewPager() {
        binding.viewPagerWorkspaces.apply {
            adapter = this@StatisticsFragment.adapter
            offscreenPageLimit = 1
            setPageTransformer(CardPeekTransformer())

            val pageMarginPx = resources.getDimensionPixelOffset(
                android.R.dimen.notification_large_icon_width
            ) / 4
            val offsetPx = resources.getDimensionPixelOffset(
                android.R.dimen.notification_large_icon_width
            ) / 8

            (getChildAt(0) as? androidx.recyclerview.widget.RecyclerView)?.apply {
                clipToPadding = false
                setPadding(offsetPx, 0, offsetPx, 0)
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.sababukia_tbc.R.id.nav_favorite -> {
                    Toast.makeText(requireContext(), "Favorite clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                com.example.sababukia_tbc.R.id.nav_home -> {
                    Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                com.example.sababukia_tbc.R.id.nav_messages -> {
                    Toast.makeText(requireContext(), "Messages clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        
        // Set home as default selected item
        binding.bottomNavigation.selectedItemId = com.example.sababukia_tbc.R.id.nav_home
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    adapter.submitList(state.workspaces)
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is StatisticsSideEffect.ShowError -> {
                            Toast.makeText(
                                requireContext(),
                                sideEffect.message.asString(this@StatisticsFragment),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
