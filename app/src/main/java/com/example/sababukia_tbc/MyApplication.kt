package com.example.sababukia_tbc

import android.app.Application
import com.example.sababukia_tbc.presentation.util.StringResourceResolver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize StringResourceResolver for accessing resources from non-Activity classes
        StringResourceResolver.initialize(this)
    }
}
