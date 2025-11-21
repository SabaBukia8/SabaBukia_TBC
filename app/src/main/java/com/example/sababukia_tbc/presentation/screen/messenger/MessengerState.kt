package com.example.sababukia_tbc.presentation.screen.messenger

import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.domain.model.ChatItem

data class MessengerState(
    val chatsResource: Resource<List<ChatItem>> = Resource.Loading(isLoading = false),
    val filteredChats: List<ChatItem> = emptyList(),
    val searchQuery: String = ""
)
