package com.example.sababukia_tbc.domain.repository

import androidx.fragment.app.FragmentActivity

interface BiometricAuthRepository {
    fun isBiometricAvailable(): Boolean
    fun authenticateWithBiometric(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    )
}
