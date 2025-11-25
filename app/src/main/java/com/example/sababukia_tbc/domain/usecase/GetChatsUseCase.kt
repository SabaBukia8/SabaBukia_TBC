package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ChatItem
import com.example.sababukia_tbc.domain.repository.IMessengerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: IMessengerRepository
) {
    operator fun invoke(): Flow<Resource<List<ChatItem>>> = repository.getChats()
}
