package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.data.DatastoreManager
import javax.inject.Inject

class ClearPendingDeepLinkUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    suspend operator fun invoke() {
        datastoreManager.clearPendingDeepLink()
    }
}
