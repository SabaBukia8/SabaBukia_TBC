package com.example.sababukia_tbc.presentation.statistics.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.ui.theme.AppColors
import com.example.sababukia_tbc.ui.theme.AppTextStyle
import com.example.sababukia_tbc.ui.theme.ApplicationTheme
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun LocationBadge(
    location: String,
    modifier: Modifier = Modifier
) {
    val colors = AppColors.current
    Row(
        modifier = modifier
            .padding(horizontal = Spacing.spacer10, vertical = Spacing.spacer6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pin),
            contentDescription = null,
            tint = colors.badgeText,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.spacer4))
        Text(
            text = location,
            color = colors.badgeText,
            style = AppTextStyle.bodyLarge
        )
    }
}

@Preview
@Composable
private fun LocationBadgePreview() {
    ApplicationTheme {
        LocationBadge(location = "Tbilisi, Georgia")
    }
}
