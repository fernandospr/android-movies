package com.github.fernandospr.movies.repository.models

data class Container<T>(
    val page: Int,
    val totalPages: Int,
    var results: List<T>
)