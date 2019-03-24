package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.network.ApiItem
import com.github.fernandospr.movies.repository.network.ApiItemsContainer
import com.github.fernandospr.movies.repository.network.ApiVideosContainer
import com.github.fernandospr.movies.repository.network.MoviesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val MOVIE_TYPE = "movie"
private const val TVSHOW_TYPE = "tv"
private const val YOUTUBE_TYPE = "YouTube"

// FIXME: If offline fetch from cache
// FIXME: Fetch configuration
class RepositoryImpl(private val service: MoviesApi) : Repository {
    override fun search(query: String,
                        page: Int,
                        callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.search(query, page)
        enqueueItemsContainerCall(call, null, callback)
    }

    override fun loadPopularMovies(page: Int,
                                   callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getPopularMovies(page)
        enqueueItemsContainerCall(call, MOVIE_TYPE, callback)
    }

    override fun loadPopularTvShows(page: Int,
                                    callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getPopularTvShows(page)
        enqueueItemsContainerCall(call, TVSHOW_TYPE, callback)
    }

    override fun loadTopRatedMovies(page: Int,
                                    callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getTopRatedMovies(page)
        enqueueItemsContainerCall(call, MOVIE_TYPE, callback)
    }

    override fun loadTopRatedTvShows(page: Int,
                                     callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getTopRatedTvShows(page)
        enqueueItemsContainerCall(call, TVSHOW_TYPE, callback)
    }

    override fun loadUpcomingMovies(page: Int,
                                    callback: RepositoryCallback<ApiItemsContainer>) {
        val call = service.getUpcomingMovies(page)
        enqueueItemsContainerCall(call, MOVIE_TYPE, callback)
    }

    private fun enqueueItemsContainerCall(call: Call<ApiItemsContainer>,
                                          mediaType: String?,
                                          callback: RepositoryCallback<ApiItemsContainer>) {
        call.enqueue(object : Callback<ApiItemsContainer> {
            override fun onResponse(call: Call<ApiItemsContainer>,
                                    response: Response<ApiItemsContainer>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        mediaType?.let {
                            body.results.forEach { it.mediaType = mediaType }
                        }
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

    override fun loadVideos(item: ApiItem,
                            page: Int,
                            callback: RepositoryCallback<ApiVideosContainer>) {
        if (item.id != null) {
            val call =
                    if (MOVIE_TYPE.equals(item.mediaType, true))
                        service.getMovieVideos(item.id)
                    else
                        service.getTvShowVideos(item.id)

            call.enqueue(object : Callback<ApiVideosContainer> {
                override fun onResponse(call: Call<ApiVideosContainer>,
                                        response: Response<ApiVideosContainer>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            body.results.filter { YOUTUBE_TYPE.equals(it.site, true) }
                            callback.onSuccess(body)
                            return
                        }
                    }

                    callback.onError()
                }

                override fun onFailure(call: Call<ApiVideosContainer>, t: Throwable) {
                    if (!call.isCanceled) {
                        callback.onError()
                    }
                }
            })
        }
    }
}