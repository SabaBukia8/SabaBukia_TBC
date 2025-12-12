package com.example.sababukia_tbc.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.example.sababukia_tbc.domain.repository.NetworkRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkRepository {

    private val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    private val _isConnected = MutableStateFlow(true)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Network available: $network")
            _isConnected.value = true
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "Network lost: $network")
            val hasConnection = checkCurrentNetworkState()
            Log.d(TAG, "After network lost, connection status: $hasConnection")
            _isConnected.value = hasConnection
        }

        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            val hasInternet = capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
            val isValidated = capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
            val isMetered = !capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_METERED
            )

            Log.d(
                TAG,
                "Capabilities changed - Internet: $hasInternet, Validated: $isValidated, Metered: $isMetered"
            )

            _isConnected.value = hasInternet && isValidated
        }
    }

    override fun startMonitoring() {
        // Check initial network state
        val initialState = checkCurrentNetworkState()
        _isConnected.value = initialState
        Log.d(TAG, "Initial network state: $initialState")

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)
        Log.d(TAG, "Network monitoring started")
    }

    private fun checkCurrentNetworkState(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d(TAG, "Network monitoring stopped")
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Callback was not registered: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "NetworkRepository"
    }
}
