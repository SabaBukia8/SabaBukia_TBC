package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.UserProvider
import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.common.toAppError
import com.example.mtgcollectionmanager.data.common.toFirestoreId
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.data.sync.CardSyncManager
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CardPricing
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.domain.model.MarketPrice
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val dao: CollectionCardDao,
    private val collectionDao: CollectionDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val cardSyncManager: CardSyncManager,
    private val userProvider: UserProvider,
    networkConnectivityManager: NetworkConnectivityManager
) : BaseRepository(networkConnectivityManager), CollectionRepository {

    private val userId: String
        get() = userProvider.getCurrentUserId()

    override suspend fun getCollectionCards(collectionId: Long): Flow<Resource<List<CollectionCard>>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                resourceFlow {
                    try {
                        val cards = fetchAndSyncCardsFromNetwork(collectionId)
                        emit(Resource.Success(cards))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.toAppError(AppError.Card.LoadFailed)))
                    }
                }
            },
            fallbackOperation = {
                resourceFlow {
                    try {
                        dao.getCardsByCollection(collectionId, userId).collect { entities ->
                            emit(Resource.Success(entities.map { it.toDomain() }))
                        }
                    } catch (_: Exception) {
                        emit(Resource.Error(AppError.Card.LoadFailed))
                    }
                }
            }
        )

    override suspend fun getCardsByColor(
        collectionId: Long,
        color: String
    ): Flow<Resource<List<CollectionCard>>> = executeNetworkOperationWithFallback(
        networkOperation = {
            resourceFlow {
                try {
                    val firestoreId = getFirestoreId(collectionId)
                    val firestoreCards = cardSyncManager.syncCards(userId, firestoreId, collectionId)

                    val filteredCards = firestoreCards
                        .filter { it.colorsJson.contains(color, ignoreCase = true) }
                        .map { it.toEntity(collectionId, userId).toDomain() }

                    emit(Resource.Success(filteredCards))
                } catch (e: Exception) {
                    emit(Resource.Error(e.toAppError(AppError.Card.LoadFailed)))
                }
            }
        },
        fallbackOperation = {
            resourceFlow {
                try {
                    dao.getCardsByColor(collectionId, userId, "%$color%").collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                } catch (_: Exception) {
                    emit(Resource.Error(AppError.Card.LoadFailed))
                }
            }
        }
    )

    override suspend fun getCardsBySet(
        collectionId: Long,
        setCode: String
    ): Flow<Resource<List<CollectionCard>>> = executeNetworkOperationWithFallback(
        networkOperation = {
            resourceFlow {
                try {
                    val firestoreId = getFirestoreId(collectionId)
                    val firestoreCards = cardSyncManager.syncCards(userId, firestoreId, collectionId)

                    val filteredCards = firestoreCards
                        .filter { it.setCode.equals(setCode, ignoreCase = true) }
                        .map { it.toEntity(collectionId, userId).toDomain() }

                    emit(Resource.Success(filteredCards))
                } catch (e: Exception) {
                    emit(Resource.Error(e.toAppError(AppError.Card.LoadFailed)))
                }
            }
        },
        fallbackOperation = {
            resourceFlow {
                try {
                    dao.getCardsBySet(collectionId, userId, setCode).collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                } catch (_: Exception) {
                    emit(Resource.Error(AppError.Card.LoadFailed))
                }
            }
        }
    )

    override suspend fun getCardsByCategory(
        collectionId: Long,
        categoryId: Long?
    ): Flow<Resource<List<CollectionCard>>> = executeNetworkOperationWithFallback(
        networkOperation = {
            resourceFlow {
                try {
                    val firestoreId = getFirestoreId(collectionId)
                    val firestoreCards = cardSyncManager.syncCards(userId, firestoreId, collectionId)

                    val filteredCards = filterCardsByCategory(firestoreCards, categoryId)
                        .map { it.toEntity(collectionId, userId).toDomain() }

                    emit(Resource.Success(filteredCards))
                } catch (e: Exception) {
                    emit(Resource.Error(e.toAppError(AppError.Card.LoadFailed)))
                }
            }
        },
        fallbackOperation = {
            resourceFlow {
                try {
                    val cardsFlow = if (categoryId == null) {
                        dao.getUncategorizedCards(collectionId, userId)
                    } else {
                        dao.getCardsByCategory(collectionId, categoryId, userId)
                    }
                    cardsFlow.collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                } catch (_: Exception) {
                    emit(Resource.Error(AppError.Card.LoadFailed))
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
    ): Flow<Resource<Unit>> = resourceFlow {
        try {
            val firestoreId = getFirestoreId(collectionId)
            val dto = createFirestoreCardDto(card, quantity, condition, notes)

            cardSyncManager.addCard(userId, firestoreId, dto)

            val entity = card.toEntity(
                collectionId = collectionId,
                categoryId = null,
                quantity = quantity,
                condition = condition,
                addedDate = System.currentTimeMillis(),
                notes = notes,
                userId = userId
            )
            dao.insertCard(entity)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Card.AddFailed)))
        }
    }

    override suspend fun removeCard(collectionId: Long, cardId: String): Flow<Resource<Unit>> =
        resourceFlow {
            try {
                val firestoreId = getFirestoreId(collectionId)

                deleteCardFromFirestore(firestoreId, cardId)
                deleteCardFromLocal(collectionId, cardId)

                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.toAppError(AppError.Card.RemoveFailed)))
            }
        }

    override suspend fun updateCardQuantity(
        collectionId: Long,
        cardId: String,
        quantity: Int
    ): Flow<Resource<Unit>> = resourceFlow {
        try {
            val firestoreId = getFirestoreId(collectionId)

            updateCardInFirestore(firestoreId, cardId, mapOf("quantity" to quantity))

            val existingCard = dao.getCardInCollection(cardId, collectionId, userId)
                ?: run {
                    emit(Resource.Error(AppError.Card.NotFound))
                    return@resourceFlow
                }

            dao.updateCard(existingCard.copy(quantity = quantity))
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Card.UpdateFailed)))
        }
    }

    override suspend fun updateCardDetails(
        collectionId: Long,
        cardId: String,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>> = resourceFlow {
        try {
            val firestoreId = getFirestoreId(collectionId)

            updateCardInFirestore(
                firestoreId,
                cardId,
                mapOf(
                    "quantity" to quantity,
                    "condition" to condition.name,
                    "notes" to notes
                )
            )

            val existingCard = dao.getCardInCollection(cardId, collectionId, userId)
                ?: run {
                    emit(Resource.Error(AppError.Card.NotFound))
                    return@resourceFlow
                }

            dao.updateCard(
                existingCard.copy(
                    quantity = quantity,
                    condition = condition.name,
                    notes = notes
                )
            )
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Card.UpdateFailed)))
        }
    }

    override suspend fun isCardInCollection(collectionId: Long, cardId: String): Boolean {
        val localCard = dao.getCardInCollection(cardId, collectionId, userId)
        if (localCard != null) return true

        return try {
            val firestoreId = getFirestoreId(collectionId)
            firestoreDataSource.getCardByCardId(userId, firestoreId, cardId) != null
        } catch (_: Exception) {
            false
        }
    }


    private suspend fun getFirestoreId(collectionId: Long): String {
        val localEntity = collectionDao.getCollectionById(collectionId, userId)
        return localEntity?.firestoreId.toFirestoreId(collectionId)
    }


    private suspend fun fetchAndSyncCardsFromNetwork(collectionId: Long): List<CollectionCard> {
        val firestoreId = getFirestoreId(collectionId)
        val firestoreCards = cardSyncManager.syncCards(userId, firestoreId, collectionId)
        return firestoreCards.map { it.toEntity(collectionId, userId).toDomain() }
    }


    private suspend fun deleteCardFromFirestore(firestoreId: String, cardId: String) {
        cardSyncManager.deleteCard(userId, firestoreId, cardId)
    }

    private suspend fun deleteCardFromLocal(collectionId: Long, cardId: String) {
        val existingCard = dao.getCardInCollection(cardId, collectionId, userId)
        existingCard?.let {
            dao.deleteCardByInternalId(it.id, userId)
        }
    }

    private suspend fun updateCardInFirestore(
        firestoreId: String,
        cardId: String,
        updates: Map<String, Any?>
    ) {
        cardSyncManager.updateCard(userId, firestoreId, cardId, updates)
    }

    private fun filterCardsByCategory(
        cards: List<FirestoreCardDto>,
        categoryId: Long?
    ): List<FirestoreCardDto> {
        return if (categoryId == null) {
            cards.filter { it.categoryId == null }
        } else {
            cards.filter { it.categoryId == categoryId.toString() }
        }
    }

    private fun createFirestoreCardDto(
        card: Card,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): FirestoreCardDto {
        return FirestoreCardDto(
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
    }

    private fun calculateDefaultPrice(pricing: CardPricing): Double {
        return pricing.markets
            .find { it.marketId == MarketPrice.MARKET_TCGPLAYER }
            ?.normalPrice
            ?: pricing.markets.firstOrNull { it.normalPrice != null }?.normalPrice
            ?: 0.0
    }
}