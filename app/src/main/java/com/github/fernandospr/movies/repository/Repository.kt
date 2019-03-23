package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.network.ApiItemsContainer

interface Repository {
    fun search(query: String,
               page: Int = 1,
               callback: RepositoryCallback<ApiItemsContainer>)
}

interface RepositoryCallback<in T> {
    fun onSuccess(t: T)
    fun onError()
}