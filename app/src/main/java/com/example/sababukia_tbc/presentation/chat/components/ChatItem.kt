package com.example.sababukia_tbc.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.model.Chat
import com.example.sababukia_tbc.domain.model.MessageType
import com.example.sababukia_tbc.ui.theme.ChatDivider
import com.example.sababukia_tbc.ui.theme.ChatPrimaryAccent
import com.example.sababukia_tbc.ui.theme.ChatTextSecondary
import com.example.sababukia_tbc.ui.theme.Spacing
import com.example.sababukia_tbc.ui.theme.White

@Composable
fun ChatItem(
    chat: Chat,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.spacer16, vertical = Spacing.spacer12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!chat.image.isNullOrEmpty()) {
                AsyncImage(
                    model = chat.image,
                    contentDescription = chat.owner,
                    modifier = Modifier
                        .size(Spacing.spacer48)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                AvatarPlaceholder(
                    name = chat.owner,
                    size = Spacing.spacer48
                )
            }

            Spacer(modifier = Modifier.width(Spacing.spacer12))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = chat.owner,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.spacer4))
                MessagePreview(chat = chat, hasUnread = chat.unreadMessages > 0)
            }

            Spacer(modifier = Modifier.width(Spacing.spacer8))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = chat.lastActive,
                    color = ChatTextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(Spacing.spacer4))
                if (chat.isTyping) {
                    TypingIndicator()
                } else if (chat.unreadMessages > 0) {
                    UnreadBadge(count = chat.unreadMessages)
                }
            }
        }
        HorizontalDivider(
            color = ChatDivider,
            thickness = Spacing.spacer2 / 2
        )
    }
}

@Composable
private fun MessagePreview(chat: Chat, hasUnread: Boolean) {
    val textColor = if (hasUnread) White else ChatTextSecondary
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (chat.lastMessageType) {
            MessageType.VOICE -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_recorder),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(Spacing.spacer16)
                )
                Spacer(modifier = Modifier.width(Spacing.spacer4))
                Text(
                    text = "Sent a voice message",
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MessageType.FILE -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_attachment),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(Spacing.spacer16)
                )
                Spacer(modifier = Modifier.width(Spacing.spacer4))
                Text(
                    text = "Sent an attachment",
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MessageType.TEXT -> {
                Text(
                    text = chat.lastMessage,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Spacing.spacer10))
            .background(ChatPrimaryAccent)
            .padding(horizontal = Spacing.spacer8, vertical = Spacing.spacer2),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = White,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TypingIndicator() {
    Text(
        text = "...",
        color = ChatPrimaryAccent,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A2F2A)
@Composable
private fun ChatItemTextPreview() {
    ChatItem(
        chat = Chat(
            id = 1,
            image = null,
            owner = "გრიშა ონიანი",
            lastMessage = "თავის ტერიტორიას ბომბავდა",
            lastActive = "4:20 PM",
            unreadMessages = 3,
            isTyping = false,
            lastMessageType = MessageType.TEXT
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A2F2A)
@Composable
private fun ChatItemVoicePreview() {
    ChatItem(
        chat = Chat(
            id = 2,
            image = null,
            owner = "ჯემალ კაკაურიძე",
            lastMessage = "შემოგევლე",
            lastActive = "3:00 AM",
            unreadMessages = 0,
            isTyping = true,
            lastMessageType = MessageType.VOICE
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A2F2A)
@Composable
private fun ChatItemFilePreview() {
    ChatItem(
        chat = Chat(
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
}
