package model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: Long,
    val trackingNumber: String,
    val quantity: Int,
    val subtotalCents: Int,
    val dateMillis: Long,
    val status: OrderStatus = OrderStatus.PENDING
) : Parcelable
