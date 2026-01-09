package com.example.sababukia_tbc.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_USER_ACTIONS_ID = "user_actions_channel"
        const val CHANNEL_GENERAL_ID = "general_channel"

        const val CHANNEL_USER_ACTIONS_NAME = "User Actions"
        const val CHANNEL_GENERAL_NAME = "General Notifications"

        private const val CHANNEL_USER_ACTIONS_DESC = "Notifications about user messages, profile updates, and interactions"
        private const val CHANNEL_GENERAL_DESC = "General app notifications and updates"
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val userActionsChannel = NotificationChannel(
                CHANNEL_USER_ACTIONS_ID,
                CHANNEL_USER_ACTIONS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_USER_ACTIONS_DESC
                enableLights(true)
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL_ID,
                CHANNEL_GENERAL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_GENERAL_DESC
                enableLights(true)
                enableVibration(false)
            }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.createNotificationChannel(userActionsChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }
}
