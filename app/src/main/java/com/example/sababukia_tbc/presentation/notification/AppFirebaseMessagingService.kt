package com.example.sababukia_tbc.presentation.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.usecase.SaveFcmTokenUseCase
import com.example.sababukia_tbc.domain.usecase.SyncFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var saveFcmTokenUseCase: SaveFcmTokenUseCase

    @Inject
    lateinit var syncFcmTokenUseCase: SyncFcmTokenUseCase

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "FCMService"

        // Notification data keys
        const val KEY_NOTIFICATION_TYPE = "type"
        const val KEY_USER_ID = "userId"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_CHANNEL_ID = "channel_id"

        // Notification types
        const val TYPE_USER_PROFILE = "user_profile"
        const val TYPE_HOME = "home"

        private var notificationId = 0
    }

    override fun onCreate() {
        super.onCreate()
        // Create notification channels when service is created
        notificationChannelManager.createNotificationChannels()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        serviceScope.launch {
            try {
                // Save token to DataStore
                saveFcmTokenUseCase(token)
                Log.d(TAG, "FCM token saved to DataStore")

                // Sync with backend (optional - may fail if API not implemented yet)
                syncFcmTokenUseCase(token).collect { resource ->
                    when (resource) {
                        is com.example.sababukia_tbc.domain.common.Resource.Success -> {
                            Log.d(TAG, "FCM token synced with backend successfully")
                        }
                        is com.example.sababukia_tbc.domain.common.Resource.Error -> {
                            Log.w(TAG, "Failed to sync FCM token with backend: ${resource.error}")
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling new FCM token", e)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        // Handle both data payload and notification payload
        message.data.takeIf { it.isNotEmpty() }?.let { data ->
            Log.d(TAG, "Message data payload: $data")
            handleDataMessage(data)
        }

        // If notification payload is present, it will be handled automatically by system
        // when app is in background. We need to handle it manually when app is in foreground.
        message.notification?.let { notification ->
            Log.d(TAG, "Message notification payload: ${notification.title}")
            handleNotificationPayload(notification, message.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data[KEY_NOTIFICATION_TYPE] ?: TYPE_HOME
        val userId = data[KEY_USER_ID]
        val title = data[KEY_TITLE] ?: getString(R.string.app_name)
        val body = data[KEY_BODY] ?: ""
        val channelId = data[KEY_CHANNEL_ID] ?: NotificationChannelManager.CHANNEL_GENERAL_ID

        showNotification(title, body, channelId, type, userId)
    }

    private fun handleNotificationPayload(
        notification: RemoteMessage.Notification,
        data: Map<String, String>
    ) {
        val type = data[KEY_NOTIFICATION_TYPE] ?: TYPE_HOME
        val userId = data[KEY_USER_ID]
        val channelId = data[KEY_CHANNEL_ID] ?: NotificationChannelManager.CHANNEL_GENERAL_ID

        showNotification(
            title = notification.title ?: getString(R.string.app_name),
            body = notification.body ?: "",
            channelId = channelId,
            type = type,
            userId = userId
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        type: String,
        userId: String?
    ) {
        // Build deep link URI based on type
        val deepLinkUri = when (type) {
            TYPE_USER_PROFILE -> {
                userId?.let { Uri.parse("sababukia://user/$it") }
                    ?: Uri.parse("sababukia://home") // Fallback if userId is null
            }
            TYPE_HOME -> Uri.parse("sababukia://home")
            else -> Uri.parse("sababukia://home")
        }

        Log.d(TAG, "Creating notification with deep link: $deepLinkUri")

        // Create intent with deep link URI
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            setPackage(packageName) // Ensure it opens YOUR app, not browser
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId++,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
