package model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: Long,
    val title: String,
    val colorName: String,
    val colorArgb: Int,
    val imageResId: Int,
    val quantity: Int,
    val subtotalCents: Int,
    val dateMillis: Long,
    val status: OrderStatus = OrderStatus.ACTIVE,
    val rating: Int? = null,
    val reviewText: String? = null
) : Parcelable