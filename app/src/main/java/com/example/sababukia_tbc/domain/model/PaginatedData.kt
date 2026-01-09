package com.example.sababukia_tbc.domain.model

data class PaginatedData<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val total: Int,
    val hasNextPage: Boolean
) {
    val hasPreviousPage: Boolean
        get() = currentPage > 1
}
