package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.model.Event
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.StoreError

interface StoreRepository {
    suspend fun getEvents(): Result<List<Event>, StoreError>
    suspend fun getCategories(): Result<List<Category>, StoreError>
}
