package com.example.sababukia_tbc.data.repository

import android.content.Context
import android.provider.Settings
import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.model.remote.dto.fcm.FcmTokenRequestDTO
import com.example.sababukia_tbc.data.model.remote.network.FcmApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.common.mapResource
import com.example.sababukia_tbc.domain.repository.FcmRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmRepositoryImpl @Inject constructor(
    private val datastoreManager: DatastoreManager,
    private val fcmApiService: FcmApiService,
    private val handleResponse: HandleResponse,
    @ApplicationContext private val context: Context
) : FcmRepository {

    override suspend fun saveFcmToken(token: String) {
        datastoreManager.saveFcmToken(token)
    }

    override suspend fun getFcmToken(): String? {
        return datastoreManager.fcmToken.first()
    }

    override fun getFcmTokenFlow(): Flow<String> {
        return datastoreManager.fcmToken
    }

    override suspend fun clearFcmToken() {
        datastoreManager.clearFcmToken()
    }

    override fun syncFcmTokenWithBackend(token: String): Flow<Resource<Boolean>> {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        return handleResponse.safeApiCall {
            fcmApiService.syncFcmToken(
                FcmTokenRequestDTO(
                    token = token,
                    deviceId = deviceId
                )
            )
        }.mapResource { it.success }
    }
}
