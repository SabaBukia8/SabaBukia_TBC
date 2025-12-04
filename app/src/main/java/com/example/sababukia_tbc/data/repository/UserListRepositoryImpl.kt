package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.local.dao.UserDao
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.mapper.toEntity
import com.example.sababukia_tbc.data.remote.api.UserListApiService
import com.example.sababukia_tbc.data.util.ConnectivityMonitor
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.UserListItem
import com.example.sababukia_tbc.domain.repository.UserListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserListRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: UserListApiService,
    private val connectivityMonitor: ConnectivityMonitor,
    private val handleResponse: HandleResponse
) : UserListRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            connectivityMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    refreshUsers()
                }
            }
        }
    }

    override fun getUsers(): Flow<List<UserListItem>> =
        userDao.getAllUsers().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun refreshUsers(): Flow<Resource<Unit>> = flow {
        val isOnline = connectivityMonitor.isOnline.first()
        if (!isOnline) {
            emit(Resource.Error("No internet connection"))
            return@flow
        }

        handleResponse.safeApiCall { apiService.getUsers() }.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val entities = resource.data.map { it.toEntity() }
                    userDao.insertUsers(entities)
                    emit(Resource.Success(Unit))
                }
                is Resource.Error -> emit(resource)
                is Resource.Loading -> emit(resource)
            }
        }
    }
}
