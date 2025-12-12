package com.example.sababukia_tbc

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.sababukia_tbc.domain.repository.NetworkRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var networkRepository: NetworkRepository

    override fun onCreate() {
        super.onCreate()

        // Observe app lifecycle to start/stop network monitoring
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                // App moved to foreground - start monitoring
                android.util.Log.d("MyApplication", "App moved to foreground, starting network monitoring")
                networkRepository.startMonitoring()
            }

            override fun onStop(owner: LifecycleOwner) {
                // App moved to background - stop monitoring
                android.util.Log.d("MyApplication", "App moved to background, stopping network monitoring")
                networkRepository.stopMonitoring()
            }
        })
    }
}
