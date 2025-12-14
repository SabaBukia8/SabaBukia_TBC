package com.example.mtgcollectionmanager.data.remote.firebase

import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCategoryDto
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCollectionDto
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreUserProfileDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createUserProfile(userId: String, profile: FirestoreUserProfileDto) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(profile.toMap())
            .await()
    }

    suspend fun getUserProfile(userId: String): FirestoreUserProfileDto? {
        val doc = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
        return doc.data?.let { FirestoreUserProfileDto.fromMap(it) }
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(updates, SetOptions.merge())
            .await()
    }

    fun getCollections(userId: String): Flow<List<FirestoreCollectionDto>> = callbackFlow {
        val listener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val collections = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { FirestoreCollectionDto.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(collections)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCollectionsOnce(userId: String): List<FirestoreCollectionDto> {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.data?.let { FirestoreCollectionDto.fromMap(doc.id, it) }
        }
    }

    suspend fun getCollectionById(userId: String, collectionId: String): FirestoreCollectionDto? {
        val doc = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .get()
            .await()
        return doc.data?.let { FirestoreCollectionDto.fromMap(doc.id, it) }
    }

    suspend fun createCollection(userId: String, collection: FirestoreCollectionDto): String {
        val docRef = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .add(collection.toMap())
            .await()
        return docRef.id
    }

    suspend fun updateCollection(userId: String, collectionId: String, updates: Map<String, Any>) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .set(updates, SetOptions.merge())
            .await()
    }

    suspend fun deleteCollection(userId: String, collectionId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .delete()
            .await()
    }

    fun getCategories(userId: String, collectionId: String): Flow<List<FirestoreCategoryDto>> =
        callbackFlow {
            val listener = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(COLLECTIONS_SUBCOLLECTION)
                .document(collectionId)
                .collection(CATEGORIES_SUBCOLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val categories = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.let { FirestoreCategoryDto.fromMap(doc.id, it) }
                    } ?: emptyList()
                    trySend(categories)
                }
            awaitClose { listener.remove() }
        }

    suspend fun getCategoriesOnce(
        userId: String,
        collectionId: String
    ): List<FirestoreCategoryDto> {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CATEGORIES_SUBCOLLECTION)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.data?.let { FirestoreCategoryDto.fromMap(doc.id, it) }
        }
    }

    suspend fun createCategory(
        userId: String,
        collectionId: String,
        category: FirestoreCategoryDto
    ): String {
        val docRef = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CATEGORIES_SUBCOLLECTION)
            .add(category.toMap())
            .await()
        return docRef.id
    }

    suspend fun updateCategory(
        userId: String,
        collectionId: String,
        categoryId: String,
        updates: Map<String, Any>
    ) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CATEGORIES_SUBCOLLECTION)
            .document(categoryId)
            .set(updates, SetOptions.merge())
            .await()
    }

    suspend fun deleteCategory(userId: String, collectionId: String, categoryId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CATEGORIES_SUBCOLLECTION)
            .document(categoryId)
            .delete()
            .await()
    }

    fun getCards(userId: String, collectionId: String): Flow<List<FirestoreCardDto>> =
        callbackFlow {
            val listener = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(COLLECTIONS_SUBCOLLECTION)
                .document(collectionId)
                .collection(CARDS_SUBCOLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val cards = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.let { FirestoreCardDto.fromMap(doc.id, it) }
                    } ?: emptyList()
                    trySend(cards)
                }
            awaitClose { listener.remove() }
        }

    suspend fun getCardsOnce(userId: String, collectionId: String): List<FirestoreCardDto> {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CARDS_SUBCOLLECTION)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.data?.let { FirestoreCardDto.fromMap(doc.id, it) }
        }
    }

    suspend fun addCard(userId: String, collectionId: String, card: FirestoreCardDto): String {
        val docRef = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CARDS_SUBCOLLECTION)
            .add(card.toMap())
            .await()
        return docRef.id
    }

    suspend fun updateCard(
        userId: String,
        collectionId: String,
        cardId: String,
        updates: Map<String, Any?>
    ) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CARDS_SUBCOLLECTION)
            .document(cardId)
            .set(updates, SetOptions.merge())
            .await()
    }

    suspend fun deleteCard(userId: String, collectionId: String, cardId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CARDS_SUBCOLLECTION)
            .document(cardId)
            .delete()
            .await()
    }

    suspend fun getCardByCardId(
        userId: String,
        collectionId: String,
        cardId: String
    ): FirestoreCardDto? {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(COLLECTIONS_SUBCOLLECTION)
            .document(collectionId)
            .collection(CARDS_SUBCOLLECTION)
            .whereEqualTo("cardId", cardId)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.let { doc ->
            doc.data?.let { FirestoreCardDto.fromMap(doc.id, it) }
        }
    }

    suspend fun deleteUserData(userId: String) {
        val userDocRef = firestore.collection(USERS_COLLECTION).document(userId)

        val collections = userDocRef.collection(COLLECTIONS_SUBCOLLECTION).get().await()
        for (collectionDoc in collections.documents) {
            val categories =
                collectionDoc.reference.collection(CATEGORIES_SUBCOLLECTION).get().await()
            for (categoryDoc in categories.documents) {
                categoryDoc.reference.delete().await()
            }
            val cards = collectionDoc.reference.collection(CARDS_SUBCOLLECTION).get().await()
            for (cardDoc in cards.documents) {
                cardDoc.reference.delete().await()
            }
            collectionDoc.reference.delete().await()
        }

        userDocRef.delete().await()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val COLLECTIONS_SUBCOLLECTION = "collections"
        private const val CATEGORIES_SUBCOLLECTION = "categories"
        private const val CARDS_SUBCOLLECTION = "cards"
    }
}
