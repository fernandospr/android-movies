package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.VideoAsset

interface Repository {
    fun search(query: String,
               page: Int = 1,
               callback: RepositoryCallback<Container<Show>>)

    fun loadPopularMovies(page: Int = 1,
                          callback: RepositoryCallback<Container<Show>>)

    fun loadPopularTvShows(page: Int = 1,
                           callback: RepositoryCallback<Container<Show>>)

    fun loadTopRatedMovies(page: Int = 1,
                           callback: RepositoryCallback<Container<Show>>)

    fun loadTopRatedTvShows(page: Int = 1,
                            callback: RepositoryCallback<Container<Show>>)

    fun loadUpcomingMovies(page: Int,
                           callback: RepositoryCallback<Container<Show>>)

    fun loadVideos(item: Show,
                   page: Int = 1,
                   callback: RepositoryCallback<Container<VideoAsset>>)

    fun stopSearch()

    fun stopPopularMovies()

    fun stopPopularTvShows()

    fun stopTopRatedMovies()

    fun stopTopRatedTvShows()

    fun stopUpcomingMovies()

    fun stopVideos()
}

interface RepositoryCallback<in T> {
    fun onSuccess(t: T)
    fun onError()
}