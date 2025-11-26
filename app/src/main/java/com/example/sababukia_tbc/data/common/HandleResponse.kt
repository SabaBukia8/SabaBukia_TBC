package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

object HandleResponse {

    fun <T> safeApiCall(call: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading(isLoading = true))

        try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    emit(Resource.Success(body))
                } ?: emit(Resource.Error(errorMessage = "Response body is null"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error(errorMessage = errorBody))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> e.message ?: "HTTP error occurred"
                is IOException -> "Network error. Please check your connection."
                else -> e.message ?: "Unknown error occurred"
            }
            emit(Resource.Error(errorMessage = errorMessage))
        } finally {
            emit(Resource.Loading(isLoading = false))
        }
    }
}
