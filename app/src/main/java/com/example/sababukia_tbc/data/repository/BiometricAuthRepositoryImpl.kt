package com.example.sababukia_tbc.data.repository

import android.content.Context
import androidx.biometric.BiometricManager
import com.example.sababukia_tbc.domain.repository.BiometricAuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BiometricAuthRepository {

    override fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
}
