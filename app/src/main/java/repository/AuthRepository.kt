package repository

import android.util.Log
import com.example.sababukia_tbc.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.ApiError
import model.LoginRequest
import model.LoginResponse
import model.RegisterRequest
import model.RegisterResponse
import network.NetworkClient
import util.StringResourceResolver

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    data class Loading(val isLoading: Boolean) : AuthResult<Nothing>()
}

class AuthRepository {

    private val apiService = NetworkClient.authApiService
    private val gson = Gson()

    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun login(request: LoginRequest): AuthResult<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Login request: ${gson.toJson(request)}")

                val response = apiService.login(request)
                Log.d(TAG, "Login response code: ${response.code()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d(TAG, "Login response body: ${gson.toJson(loginResponse)}")

                    loginResponse?.let {
                        AuthResult.Success(it)
                    } ?: AuthResult.Error(StringResourceResolver.getString(R.string.error_empty_response))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login error response: $errorBody")

                    val errorMessage = try {
                        errorBody?.let {
                            val apiError = gson.fromJson(it, ApiError::class.java)
                            apiError.error
                        } ?: StringResourceResolver.getString(R.string.http_error_format, response.code(), response.message())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing error response", e)
                        StringResourceResolver.getString(R.string.error_login_failed, response.code())
                    }
                    AuthResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login network error", e)
                AuthResult.Error(StringResourceResolver.getString(R.string.error_network, e.message ?: "Unknown error"))
            }
        }

    suspend fun register(request: RegisterRequest): AuthResult<RegisterResponse> =
        withContext(Dispatchers.IO) {
            try {
                // Create API-compatible request (ReqRes API doesn't support username field)
                val apiRequest = mapOf(
                    "email" to request.email,
                    "password" to request.password
                )
                Log.d(TAG, "Register request: ${gson.toJson(apiRequest)}")

                val response = apiService.register(apiRequest)
                Log.d(TAG, "Register response code: ${response.code()}")

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    Log.d(TAG, "Register response body: ${gson.toJson(registerResponse)}")

                    registerResponse?.let {
                        AuthResult.Success(it)
                    } ?: AuthResult.Error(StringResourceResolver.getString(R.string.error_empty_response))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Register error response: $errorBody")

                    val errorMessage = try {
                        errorBody?.let {
                            val apiError = gson.fromJson(it, ApiError::class.java)
                            apiError.error
                        } ?: StringResourceResolver.getString(R.string.http_error_format, response.code(), response.message())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing error response", e)
                        StringResourceResolver.getString(R.string.error_registration_failed, response.code())
                    }
                    AuthResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register network error", e)
                AuthResult.Error(StringResourceResolver.getString(R.string.error_network, e.message ?: "Unknown error"))
            }
        }
}
