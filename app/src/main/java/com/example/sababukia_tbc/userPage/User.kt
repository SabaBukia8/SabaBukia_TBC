package com.example.sababukia_tbc.userPage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val email: String
) : Parcelable