package com.github.fernandospr.movies.search.repository

import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import com.github.fernandospr.movies.search.repository.network.SearchApi
import io.reactivex.Single

class SearchRepositoryImpl(
    private val service: SearchApi,
    private val dao: MoviesDao,
    private val networkUtils: NetworkUtils
) : SearchRepository {

    override fun search(
        query: String,
        page: Int
    ): Single<Container<Show>> {
        return if (networkUtils.isConnectedToInternet()) {
            service.search(query, page).doOnSuccess { updateDB(it) }
        } else {
            dao.getItemsLike(query).map { Container(1, 1, it) }
        }
    }

    private fun updateDB(it: Container<Show>?) {
        if (it != null) {
            dao.insertAll(it.results)
        }
    }
}