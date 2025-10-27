package model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Parcelize
data class Address(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val addressLine: String,
    val type: AddressType = AddressType.HOUSE,
) : Parcelable

enum class AddressType { HOUSE, APARTMENT }