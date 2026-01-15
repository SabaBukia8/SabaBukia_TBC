package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : SessionRepository {

    override val currentUser: User?
        get() = firebaseAuth.currentUser?.toDomain()

    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    override fun logout() {
        firebaseAuth.signOut()
    }
}
