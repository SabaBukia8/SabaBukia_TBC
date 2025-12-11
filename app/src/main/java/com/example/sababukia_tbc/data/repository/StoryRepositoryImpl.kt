package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.local.dao.StoryDao
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.mapper.toEntity
import com.example.sababukia_tbc.data.remote.api.FeedApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val apiService: FeedApiService,
    private val storyDao: StoryDao,
    private val handleResponse: HandleResponse,
    private val networkRepository: com.example.sababukia_tbc.domain.repository.NetworkRepository
) : StoryRepository {

    override fun getStories(): Flow<Resource<List<Story>>> =
        storyDao.getAllStories().map { entities ->
            Resource.Success(entities.map { it.toDomain() })
        }

    override suspend fun refreshStories() {
        // Check connectivity before making API call
        if (!networkRepository.isConnected.value) {
            // Skip API call if offline - just use cached data
            return
        }

        handleResponse.safeApiCall { apiService.getStories() }.collect { resource ->
            if (resource is Resource.Success) {
                storyDao.apply {
                    deleteAllStories()
                    insertStories(resource.data.map { it.toEntity() })
                }
            }
        }
    }
}
