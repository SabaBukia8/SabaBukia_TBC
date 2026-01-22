package com.example.sababukia_tbc.presentation.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.presentation.common.extensions.CollectWithLifecycle
import com.example.sababukia_tbc.presentation.orders.components.OrderCard
import com.example.sababukia_tbc.presentation.orders.components.OrderStatusDialog
import com.example.sababukia_tbc.presentation.orders.components.OrderTabs
import com.example.sababukia_tbc.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    onShowSnackbar: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.sideEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is OrdersSideEffect.StatusUpdateSuccess -> {
                val message = when (effect.newStatus) {
                    com.example.sababukia_tbc.domain.model.OrderStatus.DELIVERED -> "Order marked as delivered"
                    com.example.sababukia_tbc.domain.model.OrderStatus.CANCELED -> "Order cancelled"
                    else -> "Status updated"
                }
                onShowSnackbar(message)
            }
        }
    }

    state.error?.let { error ->
        onShowSnackbar(error)
        viewModel.onEvent(OrdersEvent.DismissError)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Orders",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OrderTabs(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.onEvent(OrdersEvent.SelectTab(it)) }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        PullToRefreshBox(
                            isRefreshing = state.isRefreshing,
                            onRefresh = { viewModel.onEvent(OrdersEvent.Refresh) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (state.filteredOrders.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No orders found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = state.filteredOrders,
                                        key = { it.id }
                                    ) { order ->
                                        OrderCard(
                                            order = order,
                                            onDetailsClick = {
                                                viewModel.onEvent(OrdersEvent.OpenDialog(order))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (state.isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        if (state.showDialog && state.selectedOrder != null) {
            OrderStatusDialog(
                order = state.selectedOrder!!,
                onDismiss = { viewModel.onEvent(OrdersEvent.DismissDialog) },
                onStatusChange = { viewModel.onEvent(OrdersEvent.UpdateStatus(it)) }
            )
        }
    }
}
