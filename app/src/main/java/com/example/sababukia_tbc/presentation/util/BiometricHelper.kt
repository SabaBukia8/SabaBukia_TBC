package com.example.sababukia_tbc.presentation.util

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.sababukia_tbc.R

object BiometricHelper {

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val biometricManager = BiometricManager.from(activity)

        when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                onError(activity.getString(R.string.biometric_no_hardware))
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                onError(activity.getString(R.string.biometric_not_available))
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                onError(activity.getString(R.string.biometric_not_enrolled))
                return
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                onError(activity.getString(R.string.biometric_security_update_required))
                return
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                onError(activity.getString(R.string.biometric_unsupported))
                return
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                onError(activity.getString(R.string.biometric_status_unknown))
                return
            }
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        onError(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_title))
            .setSubtitle(activity.getString(R.string.biometric_subtitle))
            .setNegativeButtonText(activity.getString(R.string.biometric_negative_button))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
