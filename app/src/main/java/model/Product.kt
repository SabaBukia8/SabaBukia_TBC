package model

import androidx.annotation.DrawableRes

enum class CategoryType {
    PARTY, CAMPING, SPORT, CASUAL, FANCY
}

data class Product(
    @DrawableRes val image: Int,
    val title: String,
    val price: String,
    val category: CategoryType
)