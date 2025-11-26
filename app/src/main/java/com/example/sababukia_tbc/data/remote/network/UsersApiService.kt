package com.example.sababukia_tbc.data.remote.network

import com.example.sababukia_tbc.data.remote.dto.UsersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApiService {
    @GET("api/users")
    suspend fun getUsers(
        @Query("page") page: Int
    ): Response<UsersResponse>
}
