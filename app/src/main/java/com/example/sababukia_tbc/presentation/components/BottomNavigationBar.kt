package com.example.sababukia_tbc.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
            .background(AppTheme.colors.cardBackground),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Favorite (heart) - inactive
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorites",
            tint = AppTheme.colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )

        // Home - active (green)
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = AppTheme.colors.primaryAccent,
            modifier = Modifier.size(24.dp)
        )

        // Chat - inactive
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = "Chat",
            tint = AppTheme.colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )

        // Profile - inactive
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = AppTheme.colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}
