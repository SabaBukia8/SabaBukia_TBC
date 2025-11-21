package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.ChatItem
import com.example.sababukia_tbc.domain.repository.IMessengerRepository
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: IMessengerRepository
) {
    suspend operator fun invoke(): Result<List<ChatItem>> {
        return repository.getChats()
    }
}
