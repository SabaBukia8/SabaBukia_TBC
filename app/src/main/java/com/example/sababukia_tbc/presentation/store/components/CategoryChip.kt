package com.example.sababukia_tbc.presentation.store.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sababukia_tbc.ui.theme.StoreAccent
import com.example.sababukia_tbc.ui.theme.StoreCategoryBg
import com.example.sababukia_tbc.ui.theme.StoreCategoryText
import com.example.sababukia_tbc.ui.theme.White
import java.util.Locale

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) StoreAccent else StoreCategoryBg
    val textColor = if (isSelected) White else StoreCategoryText

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.replaceFirstChar { it.titlecase(Locale.getDefault()) },
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2A2A2A)
@Composable
private fun CategoryChipSelectedPreview() {
    CategoryChip(
        category = "electronics",
        isSelected = true,
        onClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2A2A2A)
@Composable
private fun CategoryChipUnselectedPreview() {
    CategoryChip(
        category = "jewelery",
        isSelected = false,
        onClick = {}
    )
}
