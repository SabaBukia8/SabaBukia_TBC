package com.example.sababukia_tbc.domain.repository

import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for network connectivity monitoring.
 * Domain layer - no Android dependencies.
 */
interface NetworkRepository {
    /**
     * Observes network connectivity status.
     * @return StateFlow<Boolean> - true if connected, false otherwise
     */
    val isConnected: StateFlow<Boolean>

    /**
     * Start monitoring network connectivity.
     * Should be called when monitoring is needed (e.g., in onStart).
     */
    fun startMonitoring()

    /**
     * Stop monitoring network connectivity.
     * Should be called when monitoring is no longer needed (e.g., in onStop).
     */
    fun stopMonitoring()
}
