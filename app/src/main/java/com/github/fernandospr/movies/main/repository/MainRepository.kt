package com.github.fernandospr.movies.main.repository

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import io.reactivex.Single

interface MainRepository {

    fun loadPopularMovies(page: Int): Single<Container<Show>>

    fun loadPopularTvShows(page: Int): Single<Container<Show>>

    fun loadTopRatedMovies(page: Int): Single<Container<Show>>

    fun loadTopRatedTvShows(page: Int): Single<Container<Show>>

    fun loadUpcomingMovies(page: Int): Single<Container<Show>>
}