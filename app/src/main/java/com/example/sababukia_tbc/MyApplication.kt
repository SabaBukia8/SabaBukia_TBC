package com.example.sababukia_tbc

import android.app.Application
import com.example.sababukia_tbc.presentation.notification.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels on app startup
        notificationChannelManager.createNotificationChannels()
    }
}
