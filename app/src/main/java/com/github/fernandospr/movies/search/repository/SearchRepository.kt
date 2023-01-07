package com.github.fernandospr.movies.search.repository

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import io.reactivex.Single

interface SearchRepository {
    fun search(
        query: String,
        page: Int
    ): Single<Container<Show>>
}