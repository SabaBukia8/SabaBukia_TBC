package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.HandleResponse
import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.common.toAppError
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val apiService: ScryfallApiService,
    private val handleResponse: HandleResponse,
    networkConnectivityManager: NetworkConnectivityManager
) : BaseRepository(networkConnectivityManager), CardRepository {

    override suspend fun searchCards(query: String): Flow<Resource<List<Card>>> =
        handleResponse.safeApiCall {
            apiService.searchCards(query)
        }.map { resource ->
            resource.mapSuccess { searchResponse ->
                searchResponse.cards.map { it.toDomain() }
            }
        }

    override suspend fun getCardById(cardId: String): Flow<Resource<Card>> =
        handleResponse.safeApiCall {
            apiService.getCardById(cardId)
        }.map { resource ->
            resource.mapSuccess { cardDto ->
                cardDto.toDomain()
            }
        }

    override fun getCardPrintings(cardName: String): Flow<Resource<List<Card>>> = resourceFlow {
        try {
            val query = "!\"$cardName\""
            val response = apiService.getCardPrintings(query)

            if (response.isSuccessful && response.body() != null) {
                val cards = response.body()!!.cards.map { it.toDomain() }
                emit(Resource.Success(cards))
            } else {
                emit(Resource.Error(AppError.Card.NotFound))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Card.SearchFailed)))
        }
    }

    private fun <T, R> Resource<T>.mapSuccess(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Resource.Success -> Resource.Success(transform(data))
            is Resource.Error -> Resource.Error(error)
            is Resource.Loading -> Resource.Loading(isLoading)
        }
    }
}