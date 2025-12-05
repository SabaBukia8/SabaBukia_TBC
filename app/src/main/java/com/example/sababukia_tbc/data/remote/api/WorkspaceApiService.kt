package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.model.remote.WorkspaceDto
import retrofit2.Response
import retrofit2.http.GET

interface WorkspaceApiService {
    @GET("v1/e3215354-6784-4bae-9bb9-25b39360971b")
    suspend fun getWorkspaces(): Response<List<WorkspaceDto>>
}
