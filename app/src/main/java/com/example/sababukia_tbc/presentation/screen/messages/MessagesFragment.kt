package com.example.sababukia_tbc.presentation.screen.messages

import androidx.fragment.app.viewModels
import com.example.sababukia_tbc.databinding.FragmentMessagesBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding>(
    FragmentMessagesBinding::inflate
) {
    private val viewModel: MessagesViewModel by viewModels()
}