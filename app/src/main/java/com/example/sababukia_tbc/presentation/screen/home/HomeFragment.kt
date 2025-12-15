package com.example.sababukia_tbc.presentation.screen.home

import androidx.fragment.app.viewModels
import com.example.sababukia_tbc.databinding.FragmentHomeBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {
    private val viewModel: HomeViewModel by viewModels()
}