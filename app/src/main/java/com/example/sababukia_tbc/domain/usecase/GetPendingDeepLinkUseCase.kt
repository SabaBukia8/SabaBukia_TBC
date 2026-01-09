package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.data.DatastoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPendingDeepLinkUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    suspend operator fun invoke(): String? {
        val link = datastoreManager.pendingDeepLink.first()
        return if (link.isNullOrEmpty()) null else link
    }
}
