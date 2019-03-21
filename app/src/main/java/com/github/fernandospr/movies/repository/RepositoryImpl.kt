package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.network.ApiSearchResultsContainer
import com.github.fernandospr.movies.repository.network.MoviesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryImpl(private val service: MoviesApi) : Repository {
    override fun search(query: String,
                        page: Int,
                        callback: RepositoryCallback<ApiSearchResultsContainer>) {
        val call = service.search(query, page)
        call.enqueue(object : Callback<ApiSearchResultsContainer> {
            override fun onResponse(call: Call<ApiSearchResultsContainer>,
                                    response: Response<ApiSearchResultsContainer>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        callback.onSuccess(body)
                        return
                    }
                }

                callback.onError()
            }

            override fun onFailure(call: Call<ApiSearchResultsContainer>, t: Throwable) {
                if (!call.isCanceled) {
                    callback.onError()
                }
            }
        })
    }
}