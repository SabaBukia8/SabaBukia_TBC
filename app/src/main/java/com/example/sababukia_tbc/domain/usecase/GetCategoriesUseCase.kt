package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    companion object {
        private const val MAX_DEPTH = 4
        private const val INITIAL_DEPTH = 0
    }

    operator fun invoke(): Flow<Resource<List<Category>>> {
        return repository.getCategories().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    Resource.Success(flattenCategories(resource.data))
                }
                is Resource.Error -> resource
                is Resource.Loading -> resource
            }
        }
    }

    private fun flattenCategories(categories: List<Category>): List<Category> {
        val flatList = mutableListOf<Category>()

        fun flatten(category: Category, currentDepth: Int) {
            if (currentDepth > MAX_DEPTH) return
            flatList.add(category.copy(depth = currentDepth))
            category.children.forEach { child ->
                flatten(child, currentDepth + 1)
            }
        }

        categories.forEach { category ->
            flatten(category, INITIAL_DEPTH)
        }

        return flatList
    }
}
