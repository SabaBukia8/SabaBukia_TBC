package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.model.remote.LocationDto
import retrofit2.Response
import retrofit2.http.GET

interface LocationApiService {
    @GET("v1/d7c6d734-6080-4045-a196-7da16339b6d7")
    suspend fun getLocations(): Response<List<LocationDto>>
}
