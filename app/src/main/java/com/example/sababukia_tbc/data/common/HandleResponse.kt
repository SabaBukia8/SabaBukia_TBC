package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.common.DomainError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleResponse @Inject constructor() {

    fun <T> safeApiCall(call: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading)

        try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    emit(Resource.Success(body))
                } ?: emit(Resource.Error(DomainError.UnknownError))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error(DomainError.GeneralError(Throwable(errorBody))))
            }
        } catch (e: Exception) {
            val domainError = when (e) {
                is HttpException -> DomainError.GeneralError(e)
                is IOException -> DomainError.NetworkError
                else -> DomainError.GeneralError(e)
            }
            emit(Resource.Error(domainError))
        } finally {
            emit(Resource.Loading)
        }
    }
}
