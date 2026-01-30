package com.example.sababukia_tbc.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.presentation.mapper.DateFormatter
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTheme.radius.radius25),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppTheme.elevation.elevation14
        )
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.spacing16)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularAvatar(
                    imageUrl = post.owner.profile,
                    contentDescription = post.owner.fullName
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = AppTheme.spacing.spacing12)
                ) {
                    Text(
                        text = post.owner.fullName,
                        style = AppTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = DateFormatter.formatEpochToDate(post.postDate),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            if (post.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.spacing12))
                PostImageGrid(images = post.images)
            }

            if (post.title.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.spacing12))
                Text(
                    text = post.title,
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.spacing12))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.spacing.spacing4 / 4)
                    .background(AppTheme.colors.separator)
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.spacing12))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing24),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Likes with heart icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likes}",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }

                // Comments with chat icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.comments}",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }

                // Share with share icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            // Write comment section (conditionally shown)
            if (post.canComment) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.spacing12))
                WriteCommentSection(canPostPhoto = post.canPostPhoto)
            }
        }
    }
}
