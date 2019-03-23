package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.network.ApiItemsContainer
import com.github.fernandospr.movies.repository.network.MoviesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryImpl(private val service: MoviesApi) : Repository {
    override fun search(query: String,
                        page: Int,
                        callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.search(query, page)
        enqueueCall(call, callback)
    }

    override fun loadPopularMovies(page: Int,
                                   callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getPopularMovies(page)
        enqueueCall(call, callback)
    }

    override fun loadPopularTvShows(page: Int,
                                    callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getPopularTvShows(page)
        enqueueCall(call, callback)
    }

    override fun loadTopRatedMovies(page: Int,
                                    callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getTopRatedMovies(page)
        enqueueCall(call, callback)
    }

    override fun loadTopRatedTvShows(page: Int,
                                     callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getTopRatedTvShows(page)
        enqueueCall(call, callback)
    }

    override fun loadUpcomingMovies(page: Int,
                                    callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getUpcomingMovies(page)
        enqueueCall(call, callback)
    }

    private fun enqueueCall(call: Call<ApiItemsContainer>,
                            callback: RepositoryCallback<ApiItemsContainer>) {
        call.enqueue(object : Callback<ApiItemsContainer> {
            override fun onResponse(call: Call<ApiItemsContainer>,
                                    response: Response<ApiItemsContainer>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        callback.onSuccess(body)
                        return
                    }
                }

                callback.onError()
            }

            override fun onFailure(call: Call<ApiItemsContainer>, t: Throwable) {
                if (!call.isCanceled) {
                    callback.onError()
                }
            }
        })
    }
}