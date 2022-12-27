package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.database.MoviesDao
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.Show.Companion.MOVIE_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.POPULAR_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.TOPRATED_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.TVSHOW_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.UPCOMING_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.YOUTUBE_TYPE
import com.github.fernandospr.movies.repository.models.VideoAsset
import com.github.fernandospr.movies.repository.network.MoviesApi
import com.github.fernandospr.movies.repository.network.NetworkUtils
import io.reactivex.Single

// TODO: Fetch configuration first
class RepositoryImpl(
    private val service: MoviesApi,
    private val dao: MoviesDao,
    private val networkUtils: NetworkUtils
) : Repository {

    override fun search(
        query: String,
        page: Int
    ): Single<Container<Show>> {
        if (networkUtils.isConnectedToInternet()) {
            return service.search(query, page).doOnSuccess { updateDB(it, null, null) }
        } else {
            return dao.getItemsLike(query).map { Container(1, 1, it) }
        }
    }

    override fun loadPopularMovies(
        page: Int
    ): Single<Container<Show>> {
        if (networkUtils.isConnectedToInternet()) {
            return service.getPopularMovies(page)
                .doOnSuccess { updateDB(it, MOVIE_TYPE, POPULAR_TYPE) }
        } else {
            return dao.getItemsByMediaAndCategory(MOVIE_TYPE, POPULAR_TYPE)
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

    override fun loadPopularTvShows(
        page: Int
    ): Single<Container<Show>> {
        if (networkUtils.isConnectedToInternet()) {
            return service.getPopularTvShows(page)
                .doOnSuccess { updateDB(it, TVSHOW_TYPE, POPULAR_TYPE) }
        } else {
            return dao.getItemsByMediaAndCategory(TVSHOW_TYPE, POPULAR_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadTopRatedMovies(
        page: Int
    ): Single<Container<Show>> {
        if (networkUtils.isConnectedToInternet()) {
            return service.getTopRatedMovies(page)
                .doOnSuccess { updateDB(it, MOVIE_TYPE, TOPRATED_TYPE) }
        } else {
            return dao.getItemsByMediaAndCategory(MOVIE_TYPE, TOPRATED_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadTopRatedTvShows(
        page: Int,
    ): Single<Container<Show>> {
        if (networkUtils.isConnectedToInternet()) {
            return service.getTopRatedTvShows(page)
                .doOnSuccess { updateDB(it, TVSHOW_TYPE, TOPRATED_TYPE) }
        } else {
            return dao.getItemsByMediaAndCategory(TVSHOW_TYPE, TOPRATED_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadUpcomingMovies(
        page: Int
    ): Single<Container<Show>> {
        if (networkUtils.isConnectedToInternet()) {
            return service.getUpcomingMovies(page)
                .doOnSuccess { updateDB(it, MOVIE_TYPE, UPCOMING_TYPE) }
        } else {
            return dao.getItemsByMediaAndCategory(MOVIE_TYPE, UPCOMING_TYPE)
                .map { Container(1, 1, it) }
        }
    }

    override fun loadVideos(
        item: Show
    ): Single<Container<VideoAsset>> {
        return if (MOVIE_TYPE.equals(item.mediaType, true)) {
            service.getMovieVideos(item.id)
        } else {
            service.getTvShowVideos(item.id)
        }.doOnSuccess {
            it.results.filter { YOUTUBE_TYPE.equals(it.site, true) }
        }
    }
}