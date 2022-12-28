package com.github.fernandospr.movies.search.repository

import com.github.fernandospr.movies.common.repository.Repository
import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import com.github.fernandospr.movies.search.repository.network.SearchApi

class SearchRepositoryImpl(
    private val service: SearchApi,
    private val dao: MoviesDao,
    networkUtils: NetworkUtils
) : Repository(networkUtils), SearchRepository {

    override fun search(
        query: String,
        page: Int
    ) = fetch(
        withInternet = { service.search(query, page).doOnSuccess { updateDB(it) } },

        withoutInternet = { dao.getItemsLike(query).map { Container(1, 1, it) } }
    )

    private fun updateDB(it: Container<Show>?) {
        if (it != null) {
            dao.insertAll(it.results)
        }
    }
}