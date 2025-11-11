package repository

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.ApiError
import model.LoginRequest
import model.LoginResponse
import model.RegisterRequest
import model.RegisterResponse
import network.NetworkClient

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

    suspend fun login(email: String, password: String): AuthResult<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                Log.d(TAG, "Login request: ${gson.toJson(request)}")

                val response = apiService.login(request)
                Log.d(TAG, "Login response code: ${response.code()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d(TAG, "Login response body: ${gson.toJson(loginResponse)}")

                    loginResponse?.let {
                        AuthResult.Success(it)
                    } ?: AuthResult.Error("Empty response from server")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login error response: $errorBody")

                    val errorMessage = try {
                        errorBody?.let {
                            val apiError = gson.fromJson(it, ApiError::class.java)
                            apiError.error
                        } ?: "HTTP ${response.code()}: ${response.message()}"
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing error response", e)
                        "Login failed: HTTP ${response.code()}"
                    }
                    AuthResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login network error", e)
                AuthResult.Error("Network error: ${e.message}")
            }
        }

    suspend fun register(email: String, password: String): AuthResult<RegisterResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(email, password)
                Log.d(TAG, "Register request: ${gson.toJson(request)}")

                val response = apiService.register(request)
                Log.d(TAG, "Register response code: ${response.code()}")

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    Log.d(TAG, "Register response body: ${gson.toJson(registerResponse)}")

                    registerResponse?.let {
                        AuthResult.Success(it)
                    } ?: AuthResult.Error("Empty response from server")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Register error response: $errorBody")

                    val errorMessage = try {
                        errorBody?.let {
                            val apiError = gson.fromJson(it, ApiError::class.java)
                            apiError.error
                        } ?: "HTTP ${response.code()}: ${response.message()}"
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing error response", e)
                        "Registration failed: HTTP ${response.code()}"
                    }
                    AuthResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register network error", e)
                AuthResult.Error("Network error: ${e.message}")
            }
        }
}