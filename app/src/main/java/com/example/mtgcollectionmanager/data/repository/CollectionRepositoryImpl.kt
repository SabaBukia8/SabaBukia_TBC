package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val dao: CollectionCardDao,
    private val authRepository: AuthRepository
) : CollectionRepository {

    private val userId: String
        get() = authRepository.getCurrentUser()?.uid ?: ""

    // TODO: Remove this temporary constant when proper multi-collection support is implemented
    private val DEFAULT_COLLECTION_ID = 1L

    override fun getCollectionCards(): Flow<Resource<List<CollectionCard>>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            dao.getCardsByCollection(DEFAULT_COLLECTION_ID, userId).collect { entities ->
                emit(Resource.Success(entities.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load collection"))
        }
    }

    override fun getCardsByColor(color: String): Flow<Resource<List<CollectionCard>>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            dao.getCardsByColor(DEFAULT_COLLECTION_ID, userId, "%$color%").collect { entities ->
                emit(Resource.Success(entities.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load cards by color"))
        }
    }

    override fun getCardsBySet(setCode: String): Flow<Resource<List<CollectionCard>>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            dao.getCardsBySet(DEFAULT_COLLECTION_ID, userId, setCode).collect { entities ->
                emit(Resource.Success(entities.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load cards by set"))
        }
    }

    override suspend fun addCard(
        card: Card,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val entity = card.toEntity(
                collectionId = DEFAULT_COLLECTION_ID,
                categoryId = null, // Default to uncategorized
                quantity = quantity,
                condition = condition,
                addedDate = System.currentTimeMillis(),
                notes = notes,
                userId = userId
            )
            dao.insertCard(entity)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add card"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun removeCard(cardId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            // Find the card by Scryfall cardId and get its internal Long id
            val existingCard = dao.getCardInCollection(cardId, DEFAULT_COLLECTION_ID, userId)
            if (existingCard != null) {
                dao.deleteCardByInternalId(existingCard.id, userId)
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Card not found in collection"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to remove card"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateCardQuantity(cardId: String, quantity: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val existingCard = dao.getCardInCollection(cardId, DEFAULT_COLLECTION_ID, userId)
            if (existingCard != null) {
                dao.updateCard(existingCard.copy(quantity = quantity))
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Card not found in collection"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update quantity"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun isCardInCollection(cardId: String): Boolean =
        dao.getCardInCollection(cardId, DEFAULT_COLLECTION_ID, userId) != null
}
