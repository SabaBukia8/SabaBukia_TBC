package com.example.sababukia_tbc.presentation.screen.notifications

import androidx.fragment.app.viewModels
import com.example.sababukia_tbc.databinding.FragmentNotificationsBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(
    FragmentNotificationsBinding::inflate
) {
    private val viewModel: NotificationsViewModel by viewModels()
}