package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CardPricing
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.data.common.UserProvider
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val dao: CollectionCardDao,
    private val collectionDao: CollectionDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val userProvider: UserProvider,
    networkConnectivityManager: NetworkConnectivityManager
) : NetworkAwareRepositoryImpl(networkConnectivityManager), CollectionRepository {

    private val userId: String
        get() = userProvider.getCurrentUserId()

    override suspend fun getCollectionCards(collectionId: Long): Flow<Resource<List<CollectionCard>>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {

                        val localEntity = collectionDao.getCollectionById(collectionId, userId)
                        val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
                        
                        val firestoreCards = firestoreDataSource.getCardsOnce(userId, firestoreId)


                        dao.deleteAllCardsForCollection(collectionId, userId)
                        firestoreCards.forEach { dto ->
                            val entity = dto.toEntity(collectionId, userId)
                            // Make sure we're setting the correct userId
                            val cardWithUserId = entity.copy(userId = userId)
                            dao.insertCard(cardWithUserId)
                        }

                        emit(Resource.Success(firestoreCards.map {
                            it.toEntity(collectionId, userId).toDomain()
                        }))
                        emit(Resource.Loading(false))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.message ?: "Failed to load collection from network"))
                        emit(Resource.Loading(false))
                    }
                }
            },
            fallbackOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        dao.getCardsByCollection(collectionId, userId).collect { entities ->
                            emit(Resource.Success(entities.map { it.toDomain() }))
                        }
                        emit(Resource.Loading(false))
                    } catch (cacheError: Exception) {
                        emit(Resource.Error(cacheError.message ?: "Failed to load collection from cache"))
                        emit(Resource.Loading(false))
                    }
                }
            }
        )

    override suspend fun getCardsByColor(
        collectionId: Long,
        color: String
    ): Flow<Resource<List<CollectionCard>>> = executeNetworkOperationWithFallback(
        networkOperation = {
            flow {
                emit(Resource.Loading(true))
                try {

                    val localEntity = collectionDao.getCollectionById(collectionId, userId)
                    val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
                    
                    val firestoreCards = firestoreDataSource.getCardsOnce(userId, firestoreId)
                    val filteredCards = firestoreCards.filter { it.colorsJson.contains(color, ignoreCase = true) }


                    firestoreCards.forEach { dto ->
                        val entity = dto.toEntity(collectionId, userId)
// Make sure we're setting the correct userId
val cardWithUserId = entity.copy(userId = userId)
dao.insertCard(cardWithUserId)
                    }

                    emit(Resource.Success(filteredCards.map {
                        it.toEntity(collectionId, userId).toDomain()
                    }))
                    emit(Resource.Loading(false))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Failed to load cards by color from network"))
                    emit(Resource.Loading(false))
                }
            }
        },
        fallbackOperation = {
            flow {
                emit(Resource.Loading(true))
                try {
                    dao.getCardsByColor(collectionId, userId, "%$color%").collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                    emit(Resource.Loading(false))
                } catch (cacheError: Exception) {
                    emit(Resource.Error(cacheError.message ?: "Failed to load cards by color from cache"))
                    emit(Resource.Loading(false))
                }
            }
        }
    )

    override suspend fun getCardsBySet(
        collectionId: Long,
        setCode: String
    ): Flow<Resource<List<CollectionCard>>> = executeNetworkOperationWithFallback(
        networkOperation = {
            flow {
                emit(Resource.Loading(true))
                try {

                    val localEntity = collectionDao.getCollectionById(collectionId, userId)
                    val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
                    
                    val firestoreCards = firestoreDataSource.getCardsOnce(userId, firestoreId)
                    val filteredCards = firestoreCards.filter { it.setCode.equals(setCode, ignoreCase = true) }


                    firestoreCards.forEach { dto ->
                        val entity = dto.toEntity(collectionId, userId)
// Make sure we're setting the correct userId
val cardWithUserId = entity.copy(userId = userId)
dao.insertCard(cardWithUserId)
                    }

                    emit(Resource.Success(filteredCards.map {
                        it.toEntity(collectionId, userId).toDomain()
                    }))
                    emit(Resource.Loading(false))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Failed to load cards by set from network"))
                    emit(Resource.Loading(false))
                }
            }
        },
        fallbackOperation = {
            flow {
                emit(Resource.Loading(true))
                try {
                    dao.getCardsBySet(collectionId, userId, setCode).collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                    emit(Resource.Loading(false))
                } catch (cacheError: Exception) {
                    emit(Resource.Error(cacheError.message ?: "Failed to load cards by set from cache"))
                    emit(Resource.Loading(false))
                }
            }
        }
    )

    override suspend fun addCard(
        collectionId: Long,
        card: Card,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val dto = FirestoreCardDto(
                cardId = card.id,
                name = card.name,
                manaCost = card.manaCost,
                imageUrl = card.imageUrl,
                type = card.type,
                rarity = card.rarity,
                setCode = card.setCode,
                setName = card.setName,
                colorsJson = Json.encodeToString(card.colors),
                price = calculateDefaultPrice(card.pricing),
                quantity = quantity,
                condition = condition.name,
                addedDate = System.currentTimeMillis(),
                notes = notes
            )


            val localEntity = collectionDao.getCollectionById(collectionId, userId)
            val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
            
            firestoreDataSource.addCard(userId, firestoreId, dto)

            val entity = card.toEntity(
                collectionId = collectionId,
                categoryId = null,
                quantity = quantity,
                condition = condition,
                addedDate = System.currentTimeMillis(),
                notes = notes,
                userId = userId
            )
            // Double check the userId is set correctly
            val cardWithUserId = entity.copy(userId = userId)
            dao.insertCard(cardWithUserId)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add card"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun removeCard(collectionId: Long, cardId: String): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading(true))
            try {

                val localEntity = collectionDao.getCollectionById(collectionId, userId)
                val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
                
                val firestoreCard =
                    firestoreDataSource.getCardByCardId(userId, firestoreId, cardId)

                if (firestoreCard != null) {
                    firestoreDataSource.deleteCard(
                        userId,
                        firestoreId,
                        firestoreCard.id
                    )
                }

                val existingCard = dao.getCardInCollection(cardId, collectionId, userId)
                if (existingCard != null) {
                    dao.deleteCardByInternalId(existingCard.id, userId)
                }

                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to remove card"))
            } finally {
                emit(Resource.Loading(false))
            }
        }

    override suspend fun updateCardQuantity(
        collectionId: Long,
        cardId: String,
        quantity: Int
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {

            val localEntity = collectionDao.getCollectionById(collectionId, userId)
            val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
            
            val firestoreCard =
                firestoreDataSource.getCardByCardId(userId, firestoreId, cardId)

            if (firestoreCard != null) {
                firestoreDataSource.updateCard(
                    userId,
                    firestoreId,
                    firestoreCard.id,
                    mapOf("quantity" to quantity)
                )
            }

            val existingCard = dao.getCardInCollection(cardId, collectionId, userId)
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

    override suspend fun getCardsByCategory(
        collectionId: Long,
        categoryId: Long?
    ): Flow<Resource<List<CollectionCard>>> = executeNetworkOperationWithFallback(
        networkOperation = {
            flow {
                emit(Resource.Loading(true))
                try {

                    val localEntity = collectionDao.getCollectionById(collectionId, userId)
                    val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
                    
                    val firestoreCards = firestoreDataSource.getCardsOnce(userId, firestoreId)
                    val filteredCards = if (categoryId == null) {
                        firestoreCards.filter { it.categoryId == null }
                    } else {
                        firestoreCards.filter { it.categoryId == categoryId.toString() }
                    }


                    firestoreCards.forEach { dto ->
                        val entity = dto.toEntity(collectionId, userId)
// Make sure we're setting the correct userId
val cardWithUserId = entity.copy(userId = userId)
dao.insertCard(cardWithUserId)
                    }

                    emit(Resource.Success(filteredCards.map {
                        it.toEntity(collectionId, userId).toDomain()
                    }))
                    emit(Resource.Loading(false))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Failed to load cards by category from network"))
                    emit(Resource.Loading(false))
                }
            }
        },
        fallbackOperation = {
            flow {
                emit(Resource.Loading(true))
                try {
                    val cardsFlow = if (categoryId == null) {
                        dao.getUncategorizedCards(collectionId, userId)
                    } else {
                        dao.getCardsByCategory(collectionId, categoryId, userId)
                    }
                    cardsFlow.collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                    emit(Resource.Loading(false))
                } catch (cacheError: Exception) {
                    emit(Resource.Error(cacheError.message ?: "Failed to load cards by category from cache"))
                    emit(Resource.Loading(false))
                }
            }
        }
    )

    override suspend fun updateCardDetails(
        collectionId: Long,
        cardId: String,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {

            val localEntity = collectionDao.getCollectionById(collectionId, userId)
            val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
            
            val firestoreCard =
                firestoreDataSource.getCardByCardId(userId, firestoreId, cardId)

            if (firestoreCard != null) {
                firestoreDataSource.updateCard(
                    userId,
                    firestoreId,
                    firestoreCard.id,
                    mapOf(
                        "quantity" to quantity,
                        "condition" to condition.name,
                        "notes" to notes
                    )
                )
            }

            val existingCard = dao.getCardInCollection(cardId, collectionId, userId)
            if (existingCard != null) {
                dao.updateCard(
                    existingCard.copy(
                        quantity = quantity,
                        condition = condition.name,
                        notes = notes
                    )
                )
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Card not found in collection"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update card details"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun isCardInCollection(collectionId: Long, cardId: String): Boolean {
        val localCard = dao.getCardInCollection(cardId, collectionId, userId)
        if (localCard != null) return true

        return try {

            val localEntity = collectionDao.getCollectionById(collectionId, userId)
            val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()
            
            firestoreDataSource.getCardByCardId(userId, firestoreId, cardId) != null
        } catch (e: Exception) {
            false
        }
    }
    
    private fun calculateDefaultPrice(pricing: CardPricing): Double {

        pricing.markets.find { it.marketId == com.example.mtgcollectionmanager.domain.model.MarketPrice.MARKET_TCGPLAYER }?.normalPrice?.let {
            return it
        }
        

        pricing.markets.firstOrNull { it.normalPrice != null }?.normalPrice?.let {
            return it
        }
        

        return 0.0
    }
}
