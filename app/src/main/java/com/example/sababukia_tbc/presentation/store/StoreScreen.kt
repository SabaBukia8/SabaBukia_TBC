package com.example.sababukia_tbc.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.model.Event
import com.example.sababukia_tbc.presentation.store.components.BottomNavBar
import com.example.sababukia_tbc.presentation.store.components.CategoryChip
import com.example.sababukia_tbc.presentation.store.components.ProductCard
import com.example.sababukia_tbc.presentation.store.components.SelectedScreen
import com.example.sababukia_tbc.ui.theme.StoreAccent
import com.example.sababukia_tbc.ui.theme.StoreBackground
import com.example.sababukia_tbc.ui.theme.StoreBackgroundEnd
import com.example.sababukia_tbc.ui.theme.StoreCategoryText
import com.example.sababukia_tbc.ui.theme.White

@Composable
fun StoreScreen(
    onNavigateToHome: () -> Unit,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StoreScreenContent(
        state = state,
        onCategorySelected = { viewModel.onEvent(StoreEvent.CategorySelected(it)) },
        onRetry = { viewModel.onEvent(StoreEvent.Retry) },
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun StoreScreenContent(
    state: StoreState,
    onCategorySelected: (String) -> Unit,
    onRetry: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(StoreBackground, StoreBackgroundEnd)
                )
            )
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = StoreAccent
                )
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error,
                        color = StoreCategoryText,
                        fontSize = 16.sp
                    )
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StoreAccent)
                    ) {
                        Text(text = "Retry", color = White)
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.categories) { category ->
                            CategoryChip(
                                category = category.name,
                                isSelected = state.selectedCategory.equals(category.name, ignoreCase = true),
                                onClick = { onCategorySelected(category.name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 80.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(state.filteredEvents) { event ->
                            ProductCard(event = event)
                        }
                    }
                }

                BottomNavBar(
                    selectedScreen = SelectedScreen.STORE,
                    onHomeClick = onNavigateToHome,
                    onStoreClick = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StoreScreenContentPreview() {
    val sampleCategories = listOf(
        Category(id = 0, name = "all"),
        Category(id = 1, name = "electronics"),
        Category(id = 2, name = "jewelery"),
        Category(id = 3, name = "men's clothing"),
        Category(id = 4, name = "women's clothing")
    )
    val sampleEvents = listOf(
        Event(
            id = 1,
            title = "Fjallraven Backpack",
            price = "109.95",
            imageUrl = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
            category = "men's clothing"
        ),
        Event(
            id = 2,
            title = "Mens Casual T-Shirt",
            price = "22.30",
            imageUrl = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
            category = "men's clothing"
        ),
        Event(
            id = 3,
            title = "Mens Cotton Jacket",
            price = "55.99",
            imageUrl = "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
            category = "men's clothing"
        ),
        Event(
            id = 4,
            title = "Solid Gold Petite Micropave",
            price = "168.00",
            imageUrl = "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_.jpg",
            category = "jewelery"
        )
    )

    StoreScreenContent(
        state = StoreState(
            events = sampleEvents,
            categories = sampleCategories,
            selectedCategory = "all"
        ),
        onCategorySelected = {},
        onRetry = {},
        onNavigateToHome = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun StoreScreenLoadingPreview() {
    StoreScreenContent(
        state = StoreState(isLoading = true),
        onCategorySelected = {},
        onRetry = {},
        onNavigateToHome = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun StoreScreenErrorPreview() {
    StoreScreenContent(
        state = StoreState(error = "Network error. Please check your connection."),
        onCategorySelected = {},
        onRetry = {},
        onNavigateToHome = {}
    )
}
