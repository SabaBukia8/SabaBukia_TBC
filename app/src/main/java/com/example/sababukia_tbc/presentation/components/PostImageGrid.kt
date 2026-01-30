package com.example.sababukia_tbc.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun PostImageGrid(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return

    val displayImages = images.take(3)

    when (displayImages.size) {
        1 -> {
            AppAsyncImage(
                imageUrl = displayImages[0],
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(AppTheme.radius.radius12)),
                contentScale = ContentScale.Crop
            )
        }
        2 -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing8)
            ) {
                displayImages.forEach { imageUrl ->
                    AppAsyncImage(
                        imageUrl = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(AppTheme.radius.radius12)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        3 -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing8)
            ) {
                // Large image on the left
                AppAsyncImage(
                    imageUrl = displayImages[0],
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(AppTheme.radius.radius12)),
                    contentScale = ContentScale.Crop
                )
                // Two smaller images stacked on the right
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing8)
                ) {
                    AppAsyncImage(
                        imageUrl = displayImages[1],
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppTheme.radius.radius12)),
                        contentScale = ContentScale.Crop
                    )
                    AppAsyncImage(
                        imageUrl = displayImages[2],
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppTheme.radius.radius12)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
