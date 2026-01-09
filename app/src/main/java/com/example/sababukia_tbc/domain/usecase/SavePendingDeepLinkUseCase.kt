package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.data.DatastoreManager
import javax.inject.Inject

class SavePendingDeepLinkUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    suspend operator fun invoke(uri: String) {
        datastoreManager.savePendingDeepLink(uri)
    }
}
