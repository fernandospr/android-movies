package com.github.fernandospr.movies.main.repository

import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.models.Show.Companion.MOVIE_TYPE
import com.github.fernandospr.movies.common.repository.models.Show.Companion.POPULAR_TYPE
import com.github.fernandospr.movies.common.repository.models.Show.Companion.TOPRATED_TYPE
import com.github.fernandospr.movies.common.repository.models.Show.Companion.TVSHOW_TYPE
import com.github.fernandospr.movies.common.repository.models.Show.Companion.UPCOMING_TYPE
import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import com.github.fernandospr.movies.main.repository.network.MainApi
import io.reactivex.Single

class MainRepositoryImpl(
    private val service: MainApi,
    private val dao: MoviesDao,
    private val networkUtils: NetworkUtils
) : MainRepository {

    override fun loadPopularMovies(
        page: Int
    ): Single<Container<Show>> {
        return if (networkUtils.isConnectedToInternet()) {
            service.getPopularMovies(page)
                .doOnSuccess { updateDB(it, MOVIE_TYPE, POPULAR_TYPE) }
        } else {
            dao.getItemsByMediaAndCategory(MOVIE_TYPE, POPULAR_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadPopularTvShows(
        page: Int
    ): Single<Container<Show>> {
        return if (networkUtils.isConnectedToInternet()) {
            service.getPopularTvShows(page)
                .doOnSuccess { updateDB(it, TVSHOW_TYPE, POPULAR_TYPE) }
        } else {
            dao.getItemsByMediaAndCategory(TVSHOW_TYPE, POPULAR_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadTopRatedMovies(
        page: Int
    ): Single<Container<Show>> {
        return if (networkUtils.isConnectedToInternet()) {
            service.getTopRatedMovies(page)
                .doOnSuccess { updateDB(it, MOVIE_TYPE, TOPRATED_TYPE) }
        } else {
            dao.getItemsByMediaAndCategory(MOVIE_TYPE, TOPRATED_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadTopRatedTvShows(
        page: Int,
    ): Single<Container<Show>> {
        return if (networkUtils.isConnectedToInternet()) {
            service.getTopRatedTvShows(page)
                .doOnSuccess { updateDB(it, TVSHOW_TYPE, TOPRATED_TYPE) }
        } else {
            dao.getItemsByMediaAndCategory(TVSHOW_TYPE, TOPRATED_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadUpcomingMovies(
        page: Int
    ): Single<Container<Show>> {
        return if (networkUtils.isConnectedToInternet()) {
            service.getUpcomingMovies(page)
                .doOnSuccess { updateDB(it, MOVIE_TYPE, UPCOMING_TYPE) }
        } else {
            dao.getItemsByMediaAndCategory(MOVIE_TYPE, UPCOMING_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    private fun updateDB(it: Container<Show>?, mediaType: String?, categoryType: String?) {
        if (it != null) {
            it.results.forEach {
                if (mediaType != null) it.mediaType = mediaType
                if (categoryType != null) it.categoryType = categoryType
            }
            dao.insertAll(it.results)
        }
    }
}