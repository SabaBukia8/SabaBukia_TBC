package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleResponse @Inject constructor() {

    fun <T> safeApiCall(call: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading(isLoading = true))

        try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    emit(Resource.Success(body))
                } ?: emit(Resource.Error(ErrorType.Network.Unknown("Response body is null")))
            } else {
                emit(Resource.Error(response.toErrorType()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.toErrorType()))
        }

        emit(Resource.Loading(isLoading = false))
    }

    private fun <T> Response<T>.toErrorType(): ErrorType {
        return when (code()) {
            401 -> ErrorType.Auth.Unauthorized
            403 -> ErrorType.Auth.SessionExpired
            in 400..499 -> ErrorType.Network.Server(code(), message())
            in 500..599 -> ErrorType.Network.Server(code(), "Server error")
            else -> ErrorType.Network.Unknown(message())
        }
    }

    private fun Exception.toErrorType(): ErrorType {
        return when (this) {
            is UnknownHostException -> ErrorType.Network.NoInternet
            is SocketTimeoutException -> ErrorType.Network.Timeout
            is IOException -> ErrorType.Network.NoInternet
            is HttpException -> ErrorType.Network.Server(code(), message())
            else -> ErrorType.Network.Unknown(localizedMessage ?: "Unknown error")
        }
    }
}
