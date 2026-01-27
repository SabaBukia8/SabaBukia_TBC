package com.example.sababukia_tbc.presentation.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.presentation.common.extensions.CollectWithLifecycle
import com.example.sababukia_tbc.presentation.statistics.components.BottomNavigationBar
import com.example.sababukia_tbc.presentation.statistics.components.ErrorContent
import com.example.sababukia_tbc.presentation.statistics.components.LoadingContent
import com.example.sababukia_tbc.presentation.statistics.components.WorkspaceCard
import com.example.sababukia_tbc.ui.theme.AppOnBackground
import com.example.sababukia_tbc.ui.theme.AppOnSurfaceVariant
import com.example.sababukia_tbc.ui.theme.AppTextStyle
import com.example.sababukia_tbc.ui.theme.ApplicationTheme
import com.example.sababukia_tbc.ui.theme.DarkBackground
import com.example.sababukia_tbc.ui.theme.DarkBackgroundStart
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun StatisticsScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.sideEffect.CollectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is StatisticsSideEffect.ShowError -> {
                snackbarHostState.showSnackbar(sideEffect.message)
            }
        }
    }

    StatisticsContent(
        state = state,
        onRetry = { viewModel.onEvent(StatisticsEvent.LoadWorkspaces) }
    )
}

@Composable
fun StatisticsContent(
    state: StatisticsState,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackgroundStart, DarkBackground),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Statistics",
                style = AppTextStyle.titleLarge,
                color = AppOnBackground,
                modifier = Modifier
                    .padding(horizontal = Spacing.spacer24, vertical = Spacing.spacer24)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> LoadingContent()
                    state.error != null && state.workspaces.isEmpty() -> ErrorContent(
                        message = state.error,
                        onRetry = onRetry
                    )
                    state.workspaces.isEmpty() -> Text(
                        text = "No workspaces found",
                        style = AppTextStyle.bodyLarge,
                        color = AppOnSurfaceVariant
                    )
                    else -> {
                        val pagerState = rememberPagerState(pageCount = { state.workspaces.size })
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            pageSpacing = Spacing.spacer24,
                            beyondViewportPageCount = 1
                        ) { page ->
                            WorkspaceCard(
                                workspace = state.workspaces[page],
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = Spacing.spacer24, vertical = Spacing.spacer56)
                            )
                        }
                    }
                }
            }

            BottomNavigationBar()
        }
    }
}

@Preview(showBackground = true, name = "Statistics Light")
@Composable
private fun StatisticsScreenLightPreview() {
    ApplicationTheme(darkTheme = false) {
        StatisticsContent(
            state = StatisticsState(
                workspaces = listOf(
                    WorkspaceItem(
                        location = "Tbilisi, Georgia",
                        altitudeM = 1200,
                        title = "Mountain Workspace",
                        image = "",
                        stars = 4,
                        price = 150
                    ),
                    WorkspaceItem(
                        location = "Batumi, Georgia",
                        altitudeM = 50,
                        title = "Seaside Office",
                        image = "",
                        stars = 5,
                        price = 200
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "Statistics Dark")
@Composable
private fun StatisticsScreenDarkPreview() {
    ApplicationTheme(darkTheme = true) {
        StatisticsContent(
            state = StatisticsState(
                workspaces = listOf(
                    WorkspaceItem(
                        location = "Tbilisi, Georgia",
                        altitudeM = 1200,
                        title = "Mountain Workspace",
                        image = "",
                        stars = 4,
                        price = 150
                    )
                )
            )
        )
    }
}
