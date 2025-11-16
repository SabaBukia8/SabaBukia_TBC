package com.example.sababukia_tbc.data.remote.network

import com.example.sababukia_tbc.data.local.ILocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val localDataSource: ILocalDataSource
) : Interceptor {

    @Volatile
    private var cachedToken: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = cachedToken ?: runBlocking {
            localDataSource.getAuthToken().also { cachedToken = it }
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
