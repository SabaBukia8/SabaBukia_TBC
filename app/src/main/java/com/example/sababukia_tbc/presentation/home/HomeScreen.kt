package com.example.sababukia_tbc.presentation.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.presentation.common.components.AppButton
import com.example.sababukia_tbc.ui.theme.SabaBukiaTBCTheme

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.RefreshAuthState)
    }

    HomeScreenContent(
        state = state,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToRegister = onNavigateToRegister,
        onLogout = { viewModel.onEvent(HomeEvent.Logout) }
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeState,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier

                .fillMaxWidth()
                .weight(0.85f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = stringResource(R.string.app_icon_description),
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.photo),
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_user_foreground),
                    contentDescription = stringResource(R.string.user_avatar_description),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.pawel_czerwinski),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.pawel_czerwinski1),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoggedIn) {
                AppButton(
                    text = stringResource(R.string.logout),
                    onClick = onLogout,
                    modifier = Modifier.width(200.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppButton(
                        text = stringResource(R.string.log_in),
                        onClick = onNavigateToLogin,
                        modifier = Modifier.weight(1f),
                        isOutlined = true
                    )
                    AppButton(
                        text = stringResource(R.string.register),
                        onClick = onNavigateToRegister,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoggedOutPreview() {
    SabaBukiaTBCTheme {
        HomeScreenContent(
            state = HomeState(isLoggedIn = false),
            onNavigateToLogin = {},
            onNavigateToRegister = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoggedInPreview() {
    SabaBukiaTBCTheme {
        HomeScreenContent(
            state = HomeState(isLoggedIn = true),
            onNavigateToLogin = {},
            onNavigateToRegister = {},
            onLogout = {}
        )
    }
}
