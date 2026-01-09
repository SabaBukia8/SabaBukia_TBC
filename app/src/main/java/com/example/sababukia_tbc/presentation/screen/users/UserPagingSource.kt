package com.example.sababukia_tbc.presentation.screen.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.first

class UserPagingSource(
    private val getUsersUseCase: GetUsersUseCase
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val currentPage = params.key ?: 1

            when (val result = getUsersUseCase(currentPage).first()) {
                is Resource.Success -> {
                    val paginatedData = result.data
                    LoadResult.Page(
                        data = paginatedData.items,
                        prevKey = if (paginatedData.hasPreviousPage) currentPage - 1 else null,
                        nextKey = if (paginatedData.hasNextPage) currentPage + 1 else null
                    )
                }
                is Resource.Error -> {
                    LoadResult.Error(Exception(result.error.toString()))
                }
                is Resource.Loading -> {
                    LoadResult.Error(Exception("Loading state encountered"))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
