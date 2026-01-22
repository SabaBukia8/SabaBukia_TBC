package com.example.sababukia_tbc.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun FormScreen(
    title: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    footerContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = Spacing.spacer16)
        ) {
            Spacer(modifier = Modifier.height(Spacing.spacer48))

            if (onNavigateBack != null) {
                BackButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(bottom = Spacing.spacer24)
                )
            }

            Text(
                text = title,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = Spacing.spacer32)
            )

            content()

            Spacer(modifier = Modifier.weight(1f))

            footerContent()

            AppButton(
                text = buttonText,
                onClick = onButtonClick,
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.spacer32)
            )
        }
    }
}
