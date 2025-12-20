package com.example.mtgcollectionmanager.data.common

import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
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
        emit(Resource.Loading(isLoading = true))
        try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    emit(Resource.Success(body))
                }
            } else {
                if (response.code() == 404) {
                    emit(Resource.Error(AppError.Card.NotFound))
                } else {
                    emit(Resource.Error(AppError.Network.ServerError))
                }
            }
        } catch (e: Exception) {
            val error = when (e) {
                is HttpException -> AppError.Network.ServerError
                is IOException -> AppError.Network.NoConnection
                else -> AppError.Unknown(e.message)
            }
            emit(Resource.Error(error))
        } finally {
            emit(Resource.Loading(isLoading = false))
        }
    }
}
