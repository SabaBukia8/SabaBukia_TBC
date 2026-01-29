package com.example.sababukia_tbc.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.ui.theme.AppColors
import com.example.sababukia_tbc.ui.theme.ApplicationTheme
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier
) {
    val colors = AppColors.current
    val navShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(14.dp, navShape, ambientColor = colors.cardShadow, spotColor = colors.cardShadow)
            .background(colors.bottomNavBackground, navShape)
            .padding(vertical = Spacing.spacer32),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = "Favorite",
                tint = colors.bottomNavUnselected,
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.bottomNavSelected),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_message),
                contentDescription = "Messages",
                tint = colors.bottomNavUnselected,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    ApplicationTheme {
        BottomNavigationBar()
    }
}
