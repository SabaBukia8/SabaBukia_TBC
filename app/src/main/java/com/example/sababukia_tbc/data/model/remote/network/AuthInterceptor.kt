package com.example.sababukia_tbc.data.model.remote.network

import com.example.sababukia_tbc.data.DatastoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val datastoreManager: DatastoreManager
) : Interceptor {

    @Volatile
    private var cachedToken: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = cachedToken ?: runBlocking {
            datastoreManager.authToken.first().also { cachedToken = it }
        }

        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }

    fun clearCache() {
        cachedToken = null
    }
}
