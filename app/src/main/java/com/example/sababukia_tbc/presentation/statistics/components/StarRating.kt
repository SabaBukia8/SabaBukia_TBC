package com.example.sababukia_tbc.presentation.statistics.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.R
import androidx.compose.ui.graphics.Color
import com.example.sababukia_tbc.ui.theme.ApplicationTheme

@Composable
fun StarRating(
    stars: Int,
    maxStars: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_star
                ),
                contentDescription = null,
                tint = if (index < stars) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun StarRatingPreview() {
    ApplicationTheme {
        StarRating(stars = 3)
    }
}
