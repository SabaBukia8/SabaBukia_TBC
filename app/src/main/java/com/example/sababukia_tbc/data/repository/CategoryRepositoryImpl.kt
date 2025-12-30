package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.service.CategoryService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val service: CategoryService,
    private val handleResponse: HandleResponse
) : CategoryRepository {

    override fun getCategories(): Flow<Resource<List<Category>>> {
        return handleResponse.safeApiCall {
            service.getCategories()
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(
                    resource.data.map { it.toDomain() }
                )
                is Resource.Error -> resource
                is Resource.Loading -> resource
            }
        }
    }
}
