package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.network.ApiItemsContainer

interface Repository {
    fun search(query: String,
               page: Int = 1,
               callback: RepositoryCallback<ApiItemsContainer>)

    fun loadPopularMovies(page: Int = 1,
                          callback: RepositoryCallback<ApiItemsContainer>)

    fun loadPopularTvShows(page: Int = 1,
                           callback: RepositoryCallback<ApiItemsContainer>)

    fun loadTopRatedMovies(page: Int = 1,
                           callback: RepositoryCallback<ApiItemsContainer>)

    fun loadTopRatedTvShows(page: Int = 1,
                            callback: RepositoryCallback<ApiItemsContainer>)

    fun loadUpcomingMovies(page: Int,
                           callback: RepositoryCallback<ApiItemsContainer>)
}

interface RepositoryCallback<in T> {
    fun onSuccess(t: T)
    fun onError()
}