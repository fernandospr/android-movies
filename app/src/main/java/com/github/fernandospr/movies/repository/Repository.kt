package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.VideoAsset
import io.reactivex.Single

interface Repository {
    fun search(
        query: String,
        page: Int
    ): Single<Container<Show>>

    fun loadPopularMovies(page: Int): Single<Container<Show>>

    fun loadPopularTvShows(page: Int): Single<Container<Show>>

    fun loadTopRatedMovies(page: Int): Single<Container<Show>>

    fun loadTopRatedTvShows(page: Int): Single<Container<Show>>

    fun loadUpcomingMovies(page: Int): Single<Container<Show>>

    fun loadVideos(
        item: Show
    ): Single<Container<VideoAsset>>
}