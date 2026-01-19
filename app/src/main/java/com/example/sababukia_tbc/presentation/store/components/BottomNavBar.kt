package com.example.sababukia_tbc.presentation.store.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.ui.theme.StoreAccent
import com.example.sababukia_tbc.ui.theme.StoreCategoryBg
import com.example.sababukia_tbc.ui.theme.StoreCategoryText

enum class SelectedScreen {
    HOME,
    STORE
}

@Composable
fun BottomNavBar(
    selectedScreen: SelectedScreen,
    onHomeClick: () -> Unit,
    onStoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(StoreCategoryBg)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            icon = Icons.Default.Home,
            isSelected = selectedScreen == SelectedScreen.HOME,
            onClick = onHomeClick
        )
        NavItem(icon = Icons.Default.Search, isSelected = false, onClick = {})
        NavItem(
            icon = Icons.Default.ShoppingCart,
            isSelected = selectedScreen == SelectedScreen.STORE,
            onClick = onStoreClick
        )
        NavItem(icon = Icons.Outlined.FavoriteBorder, isSelected = false, onClick = {})
        NavItem(icon = Icons.Default.Person, isSelected = false, onClick = {})
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) StoreAccent else StoreCategoryText

    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarStoreSelectedPreview() {
    BottomNavBar(
        selectedScreen = SelectedScreen.STORE,
        onHomeClick = {},
        onStoreClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarHomeSelectedPreview() {
    BottomNavBar(
        selectedScreen = SelectedScreen.HOME,
        onHomeClick = {},
        onStoreClick = {}
    )
}
