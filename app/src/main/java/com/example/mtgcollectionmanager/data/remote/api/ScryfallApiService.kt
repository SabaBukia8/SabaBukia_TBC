package com.example.mtgcollectionmanager.data.remote.api

import com.example.mtgcollectionmanager.data.model.remote.CardDto
import com.example.mtgcollectionmanager.data.model.remote.CardSearchResponseDto
import com.example.mtgcollectionmanager.data.model.remote.SetsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScryfallApiService {
    @GET("cards/search")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("order") order: String = "name"
    ): Response<CardSearchResponseDto>

    @GET("cards/{id}")
    suspend fun getCardById(
        @Path("id") cardId: String
    ): Response<CardDto>

    @GET("cards/search")
    suspend fun getCardPrintings(
        @Query("q") query: String,  // Format: !"exact card name"
        @Query("unique") unique: String = "prints",
        @Query("order") order: String = "released"
    ): Response<CardSearchResponseDto>

    @GET("sets")
    suspend fun getSets(): Response<SetsResponseDto>
}
