package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): User = User(
    uid = uid,
    email = email,
    displayName = displayName
)
