package com.example.sababukia_tbc.presentation.store.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sababukia_tbc.domain.model.Event
import com.example.sababukia_tbc.ui.theme.Radius
import com.example.sababukia_tbc.ui.theme.StoreCardBg
import com.example.sababukia_tbc.ui.theme.StoreCategoryText
import com.example.sababukia_tbc.ui.theme.StoreHeartRed
import com.example.sababukia_tbc.ui.theme.White

@Composable
fun ProductCard(
    event: Event,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(Radius.radius25))
                .background(StoreCardBg)
        ) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(White)
                    .clickable { isFavorite = !isFavorite },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) StoreHeartRed else StoreHeartRed.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = event.title,
            color = StoreCategoryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = if (event.price.startsWith("$")) event.price else "$${event.price}",
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2A2A2A)
@Composable
private fun ProductCardPreview() {
    ProductCard(
        event = Event(
            id = 1,
            title = "Fjallraven Backpack",
            price = "109.95",
            imageUrl = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
            category = "men's clothing"
        ),
        modifier = Modifier.padding(16.dp)
    )
}
