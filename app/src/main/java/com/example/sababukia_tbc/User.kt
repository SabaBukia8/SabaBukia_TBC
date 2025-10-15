package com.example.sababukia_tbc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val birthday: Long,
    val address: String,
    val email: String,
    val desc: String? = null
) : Parcelable
