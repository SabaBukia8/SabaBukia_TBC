package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface UserListApiService {
    @GET("v1/3668d139-e182-4fe2-b909-6259524117cb")
    suspend fun getUsers(): Response<List<UserDto>>
}
