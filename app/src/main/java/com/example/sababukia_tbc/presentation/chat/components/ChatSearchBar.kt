package com.example.sababukia_tbc.presentation.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.ui.theme.ChatPrimaryAccent
import com.example.sababukia_tbc.ui.theme.ChatSearchBarBg
import com.example.sababukia_tbc.ui.theme.ChatTextSecondary
import com.example.sababukia_tbc.ui.theme.Radius
import com.example.sababukia_tbc.ui.theme.Spacing
import com.example.sababukia_tbc.ui.theme.White

@Composable
fun ChatSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spacer16, vertical = Spacing.spacer8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .weight(1f)
                .height(Spacing.spacer52),
            textStyle = TextStyle(color = White),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search",
                    color = ChatTextSecondary
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(Spacing.spacer18)
                )
            },
            shape = RoundedCornerShape(Radius.radius25),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ChatSearchBarBg,
                unfocusedContainerColor = ChatSearchBarBg,
                cursorColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(Spacing.spacer8))

        IconButton(
            onClick = onSearchClicked,
            modifier = Modifier
                .size(Spacing.spacer48)
                .clip(RoundedCornerShape(Radius.radius12))
                .background(ChatPrimaryAccent)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Search",
                tint = White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A2F2A)
@Composable
private fun ChatSearchBarPreview() {
    ChatSearchBar(
        query = "",
        onQueryChanged = {},
        onSearchClicked = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A2F2A)
@Composable
private fun ChatSearchBarWithTextPreview() {
    ChatSearchBar(
        query = "გრიშა",
        onQueryChanged = {},
        onSearchClicked = {}
    )
}
