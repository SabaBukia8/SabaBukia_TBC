package com.example.sababukia_tbc.data.remote

import android.util.Log
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.data.remote.dto.ApiErrorDTO
import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.LoginResponseDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterResponseDTO
import com.example.sababukia_tbc.data.remote.network.LoginApiService
import com.example.sababukia_tbc.data.remote.network.RegisterApiService
import com.example.sababukia_tbc.domain.model.DomainException
import com.example.sababukia_tbc.presentation.util.StringResourceResolver
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSourceImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val registerApiService: RegisterApiService
) : IRemoteDataSource {

    private val gson = Gson()

    companion object {
        private const val TAG = "RemoteDataSource"
    }

    override suspend fun login(request: LoginRequestDTO): Result<LoginResponseDTO> {
        return safeApiCall(
            apiCall = { loginApiService.login(request) },
            onSuccess = { response ->
                Log.d(TAG, "Login successful: ${gson.toJson(response)}")
                response
            },
            operationName = "Login"
        )
    }

    override suspend fun register(request: RegisterRequestDTO): Result<RegisterResponseDTO> {
        val apiRequest = mapOf(
            "email" to request.email,
            "password" to request.password
        )

        return safeApiCall(
            apiCall = { registerApiService.register(apiRequest) },
            onSuccess = { response ->
                Log.d(TAG, "Registration successful: ${gson.toJson(response)}")
                response
            },
            operationName = "Register"
        )
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>,
        onSuccess: (T) -> T,
        operationName: String
    ): Result<T> {
        return try {
            Log.d(TAG, "$operationName request initiated")

            val response = apiCall()
            Log.d(TAG, "$operationName response code: ${response.code()}")

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Result.success(onSuccess(body))
                    } else {
                        val exception = DomainException.DataException(StringResourceResolver.getString(R.string.error_empty_response))
                        Log.e(TAG, "$operationName failed: ${exception.message}")
                        Result.failure(exception)
                    }
                }

                response.code() in 400..499 -> {
                    val errorMessage = parseErrorBody(response.errorBody()?.string())
                    val exception = when (response.code()) {
                        401 -> DomainException.AuthException(errorMessage)
                        403 -> DomainException.AuthException(StringResourceResolver.getString(R.string.error_access_forbidden, errorMessage))
                        404 -> DomainException.ClientException(404, StringResourceResolver.getString(R.string.error_resource_not_found))
                        else -> DomainException.ClientException(response.code(), errorMessage)
                    }
                    Log.e(TAG, "$operationName client error: ${exception.message}")
                    Result.failure(exception)
                }

                response.code() in 500..599 -> {
                    val errorMessage = parseErrorBody(response.errorBody()?.string())
                    val exception = DomainException.ServerException(response.code(), errorMessage)
                    Log.e(TAG, "$operationName server error: ${exception.message}")
                    Result.failure(exception)
                }

                else -> {
                    val errorMessage = parseErrorBody(response.errorBody()?.string())
                    val exception = DomainException.UnknownException(StringResourceResolver.getString(R.string.http_error_format, response.code(), errorMessage))
                    Log.e(TAG, "$operationName unknown error: ${exception.message}")
                    Result.failure(exception)
                }
            }

        } catch (e: UnknownHostException) {
            val exception = DomainException.NetworkException(StringResourceResolver.getString(R.string.error_no_internet))
            Log.e(TAG, "$operationName network error: ${exception.message}", e)
            Result.failure(exception)

        } catch (e: SocketTimeoutException) {
            val exception = DomainException.NetworkException(StringResourceResolver.getString(R.string.error_request_timeout))
            Log.e(TAG, "$operationName timeout: ${exception.message}", e)
            Result.failure(exception)

        } catch (e: IOException) {
            val exception = DomainException.NetworkException(e.message ?: StringResourceResolver.getString(R.string.error_network_io))
            Log.e(TAG, "$operationName I/O error: ${exception.message}", e)
            Result.failure(exception)

        } catch (e: JsonSyntaxException) {
            val exception = DomainException.DataException(StringResourceResolver.getString(R.string.error_invalid_response_format))
            Log.e(TAG, "$operationName parsing error: ${exception.message}", e)
            Result.failure(exception)

        } catch (e: Exception) {
            val exception = DomainException.UnknownException(e.message ?: StringResourceResolver.getString(R.string.error_unknown))
            Log.e(TAG, "$operationName unexpected error: ${exception.message}", e)
            Result.failure(exception)
        }
    }

    private fun parseErrorBody(errorBody: String?): String {
        return try {
            errorBody?.let {
                val apiError = gson.fromJson(it, ApiErrorDTO::class.java)
                apiError.error
            } ?: StringResourceResolver.getString(R.string.error_unknown)
        } catch (e: Exception) {
            Log.w(TAG, StringResourceResolver.getString(R.string.error_loading_failed), e)
            errorBody ?: StringResourceResolver.getString(R.string.error_unknown)
        }
    }
}
