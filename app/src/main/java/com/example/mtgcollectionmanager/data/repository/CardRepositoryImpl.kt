package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.HandleResponse
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val apiService: ScryfallApiService,
    private val handleResponse: HandleResponse
) : CardRepository {

    override suspend fun searchCards(query: String): Flow<Resource<List<Card>>> =
        handleResponse.safeApiCall {
            apiService.searchCards(query)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.cards.map { it.toDomain() })
                is Resource.Error -> Resource.Error(resource.errorMessage)
                is Resource.Loading -> Resource.Loading(resource.isLoading)
            }
        }

    override suspend fun getCardById(cardId: String): Flow<Resource<Card>> =
        handleResponse.safeApiCall {
            apiService.getCardById(cardId)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.toDomain())
                is Resource.Error -> Resource.Error(resource.errorMessage)
                is Resource.Loading -> Resource.Loading(resource.isLoading)
            }
        }

    override fun getCardPrintings(cardName: String): Flow<Resource<List<Card>>> =
        kotlinx.coroutines.flow.flow {
            emit(Resource.Loading(true))
            emit(Resource.Loading(false))
            try {
                // Scryfall query to get all printings of a specific card
                val query = "!\"$cardName\""
                val response = apiService.getCardPrintings(query)

                if (response.isSuccessful && response.body() != null) {
                    val cards = response.body()!!.cards.map { it.toDomain() }
                    emit(Resource.Success(cards))
                } else {
                    emit(Resource.Error(response.message() ?: "Failed to load printings"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
}
