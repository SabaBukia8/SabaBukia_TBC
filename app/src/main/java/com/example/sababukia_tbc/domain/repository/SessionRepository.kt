package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.User

interface SessionRepository {
    val currentUser: User?
    val isLoggedIn: Boolean
    fun logout()
}
