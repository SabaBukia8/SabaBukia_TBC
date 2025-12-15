package com.example.sababukia_tbc.presentation.screen.favorites

import androidx.fragment.app.viewModels
import com.example.sababukia_tbc.databinding.FragmentFavoritesBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(
    FragmentFavoritesBinding::inflate
) {
    private val viewModel: FavoritesViewModel by viewModels()
}