package com.example.sababukia_tbc.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun StoryItem(
    story: Story,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(
            width = 143.dp,
            height = 200.dp
        ),
        shape = RoundedCornerShape(AppTheme.radius.radius25),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppTheme.elevation.elevation14
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppAsyncImage(
                imageUrl = story.cover,
                contentDescription = story.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x000B0B0B),
                                Color(0xB30B0B0B)
                            ),
                            startY = 0.5f * 200f
                        )
                    )
            )

            Text(
                text = story.title,
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(AppTheme.spacing.spacing12)
            )
        }
    }
}
