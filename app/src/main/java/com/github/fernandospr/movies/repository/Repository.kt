package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.network.ApiSearchResultsContainer

interface Repository {
    fun search(query: String,
               page: Int = 1,
               callback: RepositoryCallback<ApiSearchResultsContainer>)
}

interface RepositoryCallback<in T> {
    fun onSuccess(t: T)
    fun onError()
}