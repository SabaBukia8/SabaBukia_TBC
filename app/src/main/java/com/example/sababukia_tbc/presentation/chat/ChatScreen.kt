package com.example.sababukia_tbc.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.domain.model.Chat
import com.example.sababukia_tbc.domain.model.MessageType
import com.example.sababukia_tbc.presentation.chat.components.ChatBottomNav
import com.example.sababukia_tbc.presentation.chat.components.ChatItem
import com.example.sababukia_tbc.presentation.chat.components.ChatSearchBar
import com.example.sababukia_tbc.ui.theme.ChatBackground
import com.example.sababukia_tbc.ui.theme.ChatTextSecondary

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onShowSnackbar: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreenContent(
        state = state,
        onSearchQueryChanged = { viewModel.onEvent(ChatEvent.OnSearchQueryChanged(it)) },
        onSearchClicked = { viewModel.onEvent(ChatEvent.OnSearchClicked) }
    )
    state.error?.let { error ->
        onShowSnackbar(error)
        viewModel.onEvent(ChatEvent.DismissError)
    }


}

@Composable
private fun ChatScreenContent(
    state: ChatState,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBackground)
    ) {
        ChatSearchBar(
            query = state.searchQuery,
            onQueryChanged = onSearchQueryChanged,
            onSearchClicked = onSearchClicked
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.filteredChats.isEmpty() && !state.isLoading -> {
                    Text(
                        text = if (state.searchQuery.isNotEmpty()) "No chats found" else "No chats available",
                        color = ChatTextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(
                            items = state.filteredChats,
                            key = { it.id }
                        ) { chat ->
                            ChatItem(chat = chat)
                        }
                    }
                }
            }
        }

        ChatBottomNav()
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    val previewChats = listOf(
        Chat(
            id = 1,
            image = null,
            owner = "გრიშა ონიანი",
            lastMessage = "თავის ტერიტორიას ბომბავდა",
            lastActive = "4:20 PM",
            unreadMessages = 3,
            isTyping = false,
            lastMessageType = MessageType.TEXT
        ),
        Chat(
            id = 2,
            image = null,
            owner = "ჯემალ კაკაურიძე",
            lastMessage = "შემოგევლე",
            lastActive = "3:00 AM",
            unreadMessages = 0,
            isTyping = true,
            lastMessageType = MessageType.VOICE
        ),
        Chat(
            id = 3,
            image = null,
            owner = "გურამ ჯინორია",
            lastMessage = "ცოცხალი ვარ",
            lastActive = "1:00",
            unreadMessages = 0,
            isTyping = false,
            lastMessageType = MessageType.FILE
        )
    )

    ChatScreenContent(
        state = ChatState(
            chats = previewChats,
            filteredChats = previewChats,
            searchQuery = "",
            isLoading = false,
            error = null
        ),
        onSearchQueryChanged = {},
        onSearchClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenLoadingPreview() {
    ChatScreenContent(
        state = ChatState(isLoading = true),
        onSearchQueryChanged = {},
        onSearchClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenEmptyPreview() {
    ChatScreenContent(
        state = ChatState(
            chats = emptyList(),
            filteredChats = emptyList(),
            searchQuery = "test",
            isLoading = false
        ),
        onSearchQueryChanged = {},
        onSearchClicked = {}
    )
}
