package com.example.mtgcollectionmanager.data.model.local

enum class SyncStatus {
    /**
     * Entity is fully synchronized with Firestore.
     * The local copy matches the remote copy.
     */
    SYNCED,

    /**
     * Entity has local changes that haven't been synced to Firestore yet.
     * This happens during offline mode or when a write is queued.
     */
    PENDING,

    /**
     * Sync to Firestore failed (network error, permission error, etc.).
     * These entities should be retried later.
     */
    FAILED
}
