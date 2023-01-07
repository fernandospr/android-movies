package com.github.fernandospr.movies.main.repository

import com.github.fernandospr.movies.common.repository.Repository
import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.Network
import com.github.fernandospr.movies.main.repository.network.MainApi

class MainRepositoryImpl(
    private val service: MainApi,
    private val dao: MoviesDao,
    network: Network
) : Repository(network), MainRepository {

    override fun loadPopularMovies(
        page: Int
    ) = fetch(
        online = {
            service.getPopularMovies(page)
                .doOnSuccess { updateDB(it, Show.Media.MOVIE, Show.Category.POPULAR) }
        },

        offline = {
            dao.getItemsByMediaAndCategory(Show.Media.MOVIE, Show.Category.POPULAR)
                .map { Container(1, 1, it) }
        }
    )

    override fun loadPopularTvShows(
        page: Int
    ) = fetch(
        online = {
            service.getPopularTvShows(page)
                .doOnSuccess { updateDB(it, Show.Media.TV, Show.Category.POPULAR) }
        },

        offline = {
            dao.getItemsByMediaAndCategory(Show.Media.TV, Show.Category.POPULAR)
                .map { Container(1, 1, it) }
        }
    )

    override fun loadTopRatedMovies(
        page: Int
    ) = fetch(
        online = {
            service.getTopRatedMovies(page)
                .doOnSuccess { updateDB(it, Show.Media.MOVIE, Show.Category.TOPRATED) }
        },

        offline = {
            dao.getItemsByMediaAndCategory(Show.Media.MOVIE, Show.Category.TOPRATED)
                .map { Container(1, 1, it) }
        }
    )

    override fun loadTopRatedTvShows(
        page: Int,
    ) = fetch(
        online = {
            service.getTopRatedTvShows(page)
                .doOnSuccess { updateDB(it, Show.Media.TV, Show.Category.TOPRATED) }
        },

        offline = {
            dao.getItemsByMediaAndCategory(Show.Media.TV, Show.Category.TOPRATED)
                .map { Container(1, 1, it) }
        }
    )

    override fun loadUpcomingMovies(
        page: Int
    ) = fetch(
        online = {
            service.getUpcomingMovies(page)
                .doOnSuccess { updateDB(it, Show.Media.MOVIE, Show.Category.UPCOMING) }
        },

        offline = {
            dao.getItemsByMediaAndCategory(Show.Media.MOVIE, Show.Category.UPCOMING)
                .map { Container(1, 1, it) }
        }
    )

    private fun updateDB(it: Container<Show>?, mediaType: Show.Media, categoryType: Show.Category) {
        if (it != null) {
            it.results.forEach {
                it.mediaType = mediaType
                it.categoryType = categoryType
            }
            dao.insertAll(it.results)
        }
    }
}