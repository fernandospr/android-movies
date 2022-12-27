package com.github.fernandospr.movies.repository.models

data class Container<T>(
    val page: Int,
    val totalPages: Int,
    val results: List<T>
)