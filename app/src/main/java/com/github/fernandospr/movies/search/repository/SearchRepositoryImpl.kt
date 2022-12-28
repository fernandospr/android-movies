package com.github.fernandospr.movies.search.repository

import com.github.fernandospr.movies.common.repository.Repository
import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.Network
import com.github.fernandospr.movies.search.repository.network.SearchApi

class SearchRepositoryImpl(
    private val service: SearchApi,
    private val dao: MoviesDao,
    network: Network
) : Repository(network), SearchRepository {

    override fun search(
        query: String,
        page: Int
    ) = fetch(
        online = { service.search(query, page).doOnSuccess { updateDB(it) } },

        offline = { dao.getItemsLike(query).map { Container(1, 1, it) } }
    )

    private fun updateDB(it: Container<Show>?) {
        if (it != null) {
            dao.insertAll(it.results)
        }
    }
}