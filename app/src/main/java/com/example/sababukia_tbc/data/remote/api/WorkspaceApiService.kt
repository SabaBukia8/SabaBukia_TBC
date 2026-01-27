package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.model.remote.WorkspaceDto
import retrofit2.Response
import retrofit2.http.GET

interface WorkspaceApiService {
    @GET("cards")
    suspend fun getWorkspaces(): Response<List<WorkspaceDto>>
}
