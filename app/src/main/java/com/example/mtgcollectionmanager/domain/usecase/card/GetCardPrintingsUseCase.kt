package com.example.mtgcollectionmanager.domain.usecase.card

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardPrintingsUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    operator fun invoke(cardName: String): Flow<Resource<List<Card>>> {
        return cardRepository.getCardPrintings(cardName)
    }
}
