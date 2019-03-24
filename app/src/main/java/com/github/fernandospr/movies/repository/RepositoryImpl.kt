package com.github.fernandospr.movies.repository

import android.os.AsyncTask
import com.github.fernandospr.movies.repository.database.MoviesDao
import com.github.fernandospr.movies.repository.network.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val MOVIE_TYPE = "movie"
private const val TVSHOW_TYPE = "tv"
private const val POPULAR_TYPE = "popular"
private const val TOPRATED_TYPE = "toprated"
private const val UPCOMING_TYPE = "upcoming"
private const val YOUTUBE_TYPE = "YouTube"

// FIXME: Fetch configuration
class RepositoryImpl(
    private val service: MoviesApi,
    private val dao: MoviesDao,
    private val networkUtils: NetworkUtils) : Repository {

    override fun search(
            query: String,
            page: Int,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            val call = service.search(query, page)
            enqueueItemsContainerCall(call, null, null, callback)
        } else {
            dao.getItemsLike(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(buildObserver(callback))
        }
    }

    override fun loadPopularMovies(
            page: Int,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            val call = service.getPopularMovies(page)
            enqueueItemsContainerCall(call, MOVIE_TYPE, POPULAR_TYPE, callback)
        } else {
            dao.getItemsByMediaAndCategory(MOVIE_TYPE, POPULAR_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(buildObserver(callback))
        }
    }

    override fun loadPopularTvShows(
            page: Int,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            val call = service.getPopularTvShows(page)
            enqueueItemsContainerCall(call, TVSHOW_TYPE, POPULAR_TYPE, callback)
        } else {
            dao.getItemsByMediaAndCategory(TVSHOW_TYPE, POPULAR_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(buildObserver(callback))
        }
    }

    override fun loadTopRatedMovies(
            page: Int,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            val call = service.getTopRatedMovies(page)
            enqueueItemsContainerCall(call, MOVIE_TYPE, TOPRATED_TYPE, callback)
        } else {
            dao.getItemsByMediaAndCategory(MOVIE_TYPE, TOPRATED_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(buildObserver(callback))
        }
    }

    override fun loadTopRatedTvShows(
            page: Int,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            val call = service.getTopRatedTvShows(page)
            enqueueItemsContainerCall(call, TVSHOW_TYPE, TOPRATED_TYPE, callback)
        } else {
            dao.getItemsByMediaAndCategory(TVSHOW_TYPE, TOPRATED_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(buildObserver(callback))
        }
    }

    override fun loadUpcomingMovies(
            page: Int,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            val call = service.getUpcomingMovies(page)
            enqueueItemsContainerCall(call, MOVIE_TYPE, UPCOMING_TYPE, callback)
        } else {
            dao.getItemsByMediaAndCategory(MOVIE_TYPE, UPCOMING_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(buildObserver(callback))
        }
    }

    private fun buildObserver(callback: RepositoryCallback<ApiItemsContainer>): DisposableObserver<List<ApiItem>> {
        return object : DisposableObserver<List<ApiItem>>() {
            override fun onComplete() {
                // no-op
            }

            override fun onError(e: Throwable?) {
                callback.onError()
            }

            override fun onNext(value: List<ApiItem>) {
                callback.onSuccess(ApiItemsContainer(1, 1, value))
            }
        }
    }

    private fun enqueueItemsContainerCall(
            call: Call<ApiItemsContainer>,
            mediaType: String?,
            categoryType: String?,
            callback: RepositoryCallback<ApiItemsContainer>
    ) {
        call.enqueue(object : Callback<ApiItemsContainer> {
            override fun onResponse(
                    call: Call<ApiItemsContainer>,
                    response: Response<ApiItemsContainer>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        body.results.forEach {
                            if (mediaType != null) it.mediaType = mediaType
                            if (categoryType != null) it.categoryType = categoryType
                        }
                        AsyncTask.execute {
                            dao.insertAll(body.results)
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

    override fun loadVideos(
            item: ApiItem,
            page: Int,
            callback: RepositoryCallback<ApiVideosContainer>
    ) {
        val call =
                if (MOVIE_TYPE.equals(item.mediaType, true))
                    service.getMovieVideos(item.id)
                else
                    service.getTvShowVideos(item.id)

        call.enqueue(object : Callback<ApiVideosContainer> {
            override fun onResponse(
                    call: Call<ApiVideosContainer>,
                    response: Response<ApiVideosContainer>
            ) {
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