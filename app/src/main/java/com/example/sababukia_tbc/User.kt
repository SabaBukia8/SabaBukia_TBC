package com.example.sababukia_tbc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val age: String,
    val email: String
) : Parcelable
