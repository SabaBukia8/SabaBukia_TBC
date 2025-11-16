package com.example.sababukia_tbc.data.remote

import android.util.Log
import com.example.sababukia_tbc.data.remote.dto.ApiErrorDTO
import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.LoginResponseDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterResponseDTO
import com.example.sababukia_tbc.data.remote.dto.UsersResponseDTO
import com.example.sababukia_tbc.data.remote.network.LoginApiService
import com.example.sababukia_tbc.data.remote.network.RegisterApiService
import com.example.sababukia_tbc.data.remote.network.UsersApiService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val registerApiService: RegisterApiService,
    private val usersApiService: UsersApiService,
    private val gson: Gson
) : IAuthRemoteDataSource {

    override suspend fun login(request: LoginRequestDTO): Result<LoginResponseDTO> {
        return handleApiCall {
            loginApiService.login(request)
        }
    }

    override suspend fun register(request: RegisterRequestDTO): Result<RegisterResponseDTO> {
        return handleApiCall {
            registerApiService.register(request)
        }
    }

    override suspend fun getUsers(page: Int): Result<UsersResponseDTO> {
        return handleApiCall {
            usersApiService.getUsers(page)
        }
    }

    private suspend fun <T> handleApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            Log.d("AuthRemoteDataSource", "Response code: ${response.code()}")

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Log.d("AuthRemoteDataSource", "Success: $body")
                        Result.success(body)
                    } else {
                        Log.e("AuthRemoteDataSource", "Empty response body")
                        Result.failure(Exception("Empty response body"))
                    }
                }
                response.code() in 400..499 -> {
                    val errorMessage = parseErrorMessage(response)
                    Log.e("AuthRemoteDataSource", "Client error: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
                response.code() in 500..599 -> {
                    val errorMessage = parseErrorMessage(response)
                    Log.e("AuthRemoteDataSource", "Server error: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
                else -> {
                    Log.e("AuthRemoteDataSource", "Unknown error: ${response.code()}")
                    Result.failure(Exception("Unknown error occurred"))
                }
            }
        } catch (e: UnknownHostException) {
            Log.e("AuthRemoteDataSource", "Network error: No internet connection", e)
            Result.failure(Exception("No internet connection"))
        } catch (e: SocketTimeoutException) {
            Log.e("AuthRemoteDataSource", "Network error: Request timeout", e)
            Result.failure(Exception("Request timeout"))
        } catch (e: IOException) {
            Log.e("AuthRemoteDataSource", "Network error: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: JsonSyntaxException) {
            Log.e("AuthRemoteDataSource", "JSON parsing error", e)
            Result.failure(Exception("Failed to parse response"))
        } catch (e: Exception) {
            Log.e("AuthRemoteDataSource", "Unexpected error: ${e.message}", e)
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            val apiError = gson.fromJson(errorBody, ApiErrorDTO::class.java)
            apiError.error ?: "Unknown error"
        } catch (e: Exception) {
            Log.e("AuthRemoteDataSource", "Error parsing error message", e)
            "Unknown error occurred"
        }
    }
}
