package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val dao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) : CollectionRepository {

    private val userId: String
        get() = authRepository.getCurrentUser()?.uid ?: ""

    override fun getCollectionCards(collectionId: Long): Flow<Resource<List<CollectionCard>>> =
        flow {
            emit(Resource.Loading(true))
            try {
                val firestoreCards =
                    firestoreDataSource.getCardsOnce(userId, collectionId.toString())

                dao.deleteAllCardsForCollection(collectionId, userId)
                firestoreCards.forEach { dto ->
                    val entity = dto.toEntity(collectionId, userId)
                    dao.insertCard(entity)
                }

                emit(Resource.Success(firestoreCards.map {
                    it.toEntity(collectionId, userId).toDomain()
                }))
                emit(Resource.Loading(false))
            } catch (e: Exception) {
                emit(Resource.Loading(false))
                try {
                    dao.getCardsByCollection(collectionId, userId).collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                } catch (cacheError: Exception) {
                    emit(Resource.Error(e.message ?: "Failed to load collection"))
                }
            }
        }

    override fun getCardsByColor(
        collectionId: Long,
        color: String
    ): Flow<Resource<List<CollectionCard>>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCards = firestoreDataSource.getCardsOnce(userId, collectionId.toString())
            val filteredCards =
                firestoreCards.filter { it.colorsJson.contains(color, ignoreCase = true) }

            firestoreCards.forEach { dto ->
                val entity = dto.toEntity(collectionId, userId)
                dao.insertCard(entity)
            }

            emit(Resource.Success(filteredCards.map {
                it.toEntity(collectionId, userId).toDomain()
            }))
            emit(Resource.Loading(false))
        } catch (e: Exception) {
            emit(Resource.Loading(false))
            try {
                dao.getCardsByColor(collectionId, userId, "%$color%").collect { entities ->
                    emit(Resource.Success(entities.map { it.toDomain() }))
                }
            } catch (cacheError: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load cards by color"))
            }
        }
    }

    override fun getCardsBySet(
        collectionId: Long,
        setCode: String
    ): Flow<Resource<List<CollectionCard>>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCards = firestoreDataSource.getCardsOnce(userId, collectionId.toString())
            val filteredCards =
                firestoreCards.filter { it.setCode.equals(setCode, ignoreCase = true) }

            firestoreCards.forEach { dto ->
                val entity = dto.toEntity(collectionId, userId)
                dao.insertCard(entity)
            }

            emit(Resource.Success(filteredCards.map {
                it.toEntity(collectionId, userId).toDomain()
            }))
            emit(Resource.Loading(false))
        } catch (e: Exception) {
            emit(Resource.Loading(false))
            try {
                dao.getCardsBySet(collectionId, userId, setCode).collect { entities ->
                    emit(Resource.Success(entities.map { it.toDomain() }))
                }
            } catch (cacheError: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load cards by set"))
            }
        }
    }

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
                colorsJson = card.colors.joinToString(","),
                price = card.price,
                quantity = quantity,
                condition = condition.name,
                addedDate = System.currentTimeMillis(),
                notes = notes
            )

            firestoreDataSource.addCard(userId, collectionId.toString(), dto)

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
            emit(Resource.Error(e.message ?: "Failed to add card"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun removeCard(collectionId: Long, cardId: String): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading(true))
            try {
                val firestoreCard =
                    firestoreDataSource.getCardByCardId(userId, collectionId.toString(), cardId)

                if (firestoreCard != null) {
                    firestoreDataSource.deleteCard(
                        userId,
                        collectionId.toString(),
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
            val firestoreCard =
                firestoreDataSource.getCardByCardId(userId, collectionId.toString(), cardId)

            if (firestoreCard != null) {
                firestoreDataSource.updateCard(
                    userId,
                    collectionId.toString(),
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

    override fun getCardsByCategory(
        collectionId: Long,
        categoryId: Long?
    ): Flow<Resource<List<CollectionCard>>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCards = firestoreDataSource.getCardsOnce(userId, collectionId.toString())
            val filteredCards = if (categoryId == null) {
                firestoreCards.filter { it.categoryId == null }
            } else {
                firestoreCards.filter { it.categoryId == categoryId.toString() }
            }

            firestoreCards.forEach { dto ->
                val entity = dto.toEntity(collectionId, userId)
                dao.insertCard(entity)
            }

            emit(Resource.Success(filteredCards.map {
                it.toEntity(collectionId, userId).toDomain()
            }))
            emit(Resource.Loading(false))
        } catch (e: Exception) {
            emit(Resource.Loading(false))
            try {
                val cardsFlow = if (categoryId == null) {
                    dao.getUncategorizedCards(collectionId, userId)
                } else {
                    dao.getCardsByCategory(collectionId, categoryId, userId)
                }
                cardsFlow.collect { entities ->
                    emit(Resource.Success(entities.map { it.toDomain() }))
                }
            } catch (cacheError: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load cards by category"))
            }
        }
    }

    override suspend fun updateCardDetails(
        collectionId: Long,
        cardId: String,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCard =
                firestoreDataSource.getCardByCardId(userId, collectionId.toString(), cardId)

            if (firestoreCard != null) {
                firestoreDataSource.updateCard(
                    userId,
                    collectionId.toString(),
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
            firestoreDataSource.getCardByCardId(userId, collectionId.toString(), cardId) != null
        } catch (e: Exception) {
            false
        }
    }
}
