package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.remote.dto.toDomain
import com.example.sababukia_tbc.data.remote.network.MessengerApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ChatItem
import com.example.sababukia_tbc.domain.repository.IMessengerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessengerRepositoryImpl @Inject constructor(
    private val apiService: MessengerApiService
) : IMessengerRepository {

    override fun getChats(): Flow<Resource<List<ChatItem>>> =
        HandleResponse.safeApiCall {
            apiService.getChats()
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.toDomain())
                is Resource.Error -> resource
                is Resource.Loading -> resource
            }
        }
}
