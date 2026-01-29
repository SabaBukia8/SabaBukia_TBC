package com.example.sababukia_tbc.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.ui.theme.AppColors
import com.example.sababukia_tbc.ui.theme.AppTextStyle
import com.example.sababukia_tbc.ui.theme.ApplicationTheme
import com.example.sababukia_tbc.ui.theme.Radius
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun WorkspaceCard(
    workspace: WorkspaceItem,
    modifier: Modifier = Modifier
) {
    val colors = AppColors.current
    val cardShape = RoundedCornerShape(Radius.radius25)
    Box(
        modifier = modifier
            .shadow(
                elevation = 40.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.9f),
                spotColor = Color.Black.copy(alpha = 0.9f)
            )
            .clip(cardShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.cardGreen)
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(workspace.image)
                .crossfade(true)
                .build(),
            contentDescription = workspace.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.35f to Color(0x001A3B34),
                            0.75f to Color(0x800B0B0B),
                            1.0f to Color(0xCC0B0B0B)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.spacer16),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LocationBadge(location = workspace.location)
            AltitudeBadge(altitude = workspace.altitudeM)
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(Spacing.spacer24)
        ) {
            Text(
                text = workspace.title,
                style = AppTextStyle.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(Spacing.spacer12))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${workspace.price}",
                    style = AppTextStyle.titleLarge,
                    color = Color.White
                )
                StarRating(stars = workspace.stars)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkspaceCardPreview() {
    ApplicationTheme {
        WorkspaceCard(
            workspace = WorkspaceItem(
                location = "Tbilisi, Georgia",
                altitudeM = 1200,
                title = "Mountain Workspace",
                image = "",
                stars = 4,
                price = 150
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
    }
}
