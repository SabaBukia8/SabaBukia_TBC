package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.local.dao.PostDao
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.mapper.toEntity
import com.example.sababukia_tbc.data.remote.api.FeedApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: FeedApiService,
    private val postDao: PostDao,
    private val handleResponse: HandleResponse,
    private val networkRepository: com.example.sababukia_tbc.domain.repository.NetworkRepository
) : PostRepository {

    override fun getPosts(): Flow<Resource<List<Post>>> =
        postDao.getAllPosts().map { entities ->
            Resource.Success(entities.map { it.toDomain() })
        }

    override suspend fun refreshPosts() {
        // Check connectivity before making API call
        if (!networkRepository.isConnected.value) {
            // Skip API call if offline - just use cached data
            return
        }

        handleResponse.safeApiCall { apiService.getPosts() }.collect { resource ->
            if (resource is Resource.Success) {
                postDao.apply {
                    deleteAllPosts()
                    insertPosts(resource.data.map { it.toEntity() })
                }
            }
        }
    }
}
