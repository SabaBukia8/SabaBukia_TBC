package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleResponse @Inject constructor() {

    companion object {
        private const val ERROR_NO_INTERNET = "No Internet Connection"
        private const val ERROR_SERVER_PREFIX = "Server Error: "
        private const val ERROR_UNEXPECTED_PREFIX = "Unexpected Error: "
    }

    fun <T> safeApiCall(apiCall: suspend () -> T): Flow<Resource<T>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(apiCall()))
        } catch (e: Exception) {
            emit(
                when (e) {
                    is IOException -> Resource.Error(ERROR_NO_INTERNET)
                    is HttpException -> Resource.Error("$ERROR_SERVER_PREFIX${e.code()}")
                    else -> Resource.Error("$ERROR_UNEXPECTED_PREFIX${e.localizedMessage}")
                }
            )
        }
    }
}
