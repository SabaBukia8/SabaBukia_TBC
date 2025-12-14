package com.example.mtgcollectionmanager.presentation.screen.collection.drawer

sealed class CategoryDrawerItem {
    data object AllCards : CategoryDrawerItem()
    data object Uncategorized : CategoryDrawerItem()
    data class Category(
        val id: Long,
        val name: String,
        val color: String,
        val cardCount: Int
    ) : CategoryDrawerItem()
}
