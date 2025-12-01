package com.example.sababukia_tbc.data.model.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.model.remote.network.UsersApiService
import com.example.sababukia_tbc.domain.model.User

class UserPagingSource(
    private val apiService: UsersApiService
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val currentPage = params.key ?: 1
            val response = apiService.getUsers(page = currentPage)

            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    LoadResult.Page(
                        data = it.data.toDomain(),
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (currentPage < it.totalPages) currentPage + 1 else null
                    )
                } ?: LoadResult.Error(Exception("Response body is null"))
            } else {
                LoadResult.Error(Exception(response.errorBody()?.string() ?: "Unknown error"))
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
