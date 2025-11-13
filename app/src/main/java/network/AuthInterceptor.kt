package network

import data.DatastoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val datastoreManager: DatastoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get token from DataStore
        val token = runBlocking {
            datastoreManager.authToken.first()
        }

        // If token exists, add it to the request
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
