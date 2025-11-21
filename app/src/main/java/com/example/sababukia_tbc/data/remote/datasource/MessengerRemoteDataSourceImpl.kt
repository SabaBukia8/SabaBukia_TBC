package com.example.sababukia_tbc.data.remote.datasource

import com.example.sababukia_tbc.data.remote.dto.ChatItemDTO
import com.example.sababukia_tbc.data.remote.network.MessengerApiService
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessengerRemoteDataSourceImpl @Inject constructor(
    private val messengerApiService: MessengerApiService,
    private val json: Json
) : IMessengerRemoteDataSource {

    override suspend fun getChats(): Result<List<ChatItemDTO>> {
        return handleApiCall {
            messengerApiService.getChats()
        }
    }

    private suspend fun <T> handleApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
