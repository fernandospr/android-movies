package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.Show.Companion.MOVIE_TYPE
import com.github.fernandospr.movies.repository.Show.Companion.POPULAR_TYPE
import com.github.fernandospr.movies.repository.Show.Companion.TOPRATED_TYPE
import com.github.fernandospr.movies.repository.Show.Companion.TVSHOW_TYPE
import com.github.fernandospr.movies.repository.Show.Companion.UPCOMING_TYPE
import com.github.fernandospr.movies.repository.Show.Companion.YOUTUBE_TYPE
import com.github.fernandospr.movies.repository.database.MoviesDao
import com.github.fernandospr.movies.repository.network.MoviesApi
import com.github.fernandospr.movies.repository.network.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

// FIXME: Fetch configuration
class RepositoryImpl(
    private val service: MoviesApi,
    private val dao: MoviesDao,
    private val networkUtils: NetworkUtils,
    private val diskIOExecutor: Executor) : Repository {

    override fun search(
            query: String,
            page: Int,
            callback: RepositoryCallback<Container<Show>>
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
            callback: RepositoryCallback<Container<Show>>
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
            callback: RepositoryCallback<Container<Show>>
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
            callback: RepositoryCallback<Container<Show>>
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
            callback: RepositoryCallback<Container<Show>>
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
            callback: RepositoryCallback<Container<Show>>
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

    private fun buildObserver(callback: RepositoryCallback<Container<Show>>): DisposableObserver<List<Show>> {
        return object : DisposableObserver<List<Show>>() {
            override fun onComplete() {
                // no-op
            }

            override fun onError(e: Throwable?) {
                callback.onError()
            }

            override fun onNext(value: List<Show>) {
                callback.onSuccess(Container<Show>(1, 1, value))
            }
        }
    }

    private fun enqueueItemsContainerCall(
            call: Call<Container<Show>>,
            mediaType: String?,
            categoryType: String?,
            callback: RepositoryCallback<Container<Show>>
    ) {
        call.enqueue(object : Callback<Container<Show>> {
            override fun onResponse(
                    call: Call<Container<Show>>,
                    response: Response<Container<Show>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        body.results.forEach {
                            if (mediaType != null) it.mediaType = mediaType
                            if (categoryType != null) it.categoryType = categoryType
                        }

                        diskIOExecutor.execute {
                            dao.insertAll(body.results)
                        }
                        callback.onSuccess(body)
                        return
                    }
                }

                callback.onError()
            }

            override fun onFailure(call: Call<Container<Show>>, t: Throwable) {
                if (!call.isCanceled) {
                    callback.onError()
                }
            }
        })
    }

    override fun loadVideos(
            item: Show,
            page: Int,
            callback: RepositoryCallback<Container<VideoAsset>>
    ) {
        val call =
                if (MOVIE_TYPE.equals(item.mediaType, true))
                    service.getMovieVideos(item.id)
                else
                    service.getTvShowVideos(item.id)

        call.enqueue(object : Callback<Container<VideoAsset>> {
            override fun onResponse(
                    call: Call<Container<VideoAsset>>,
                    response: Response<Container<VideoAsset>>
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

            override fun onFailure(call: Call<Container<VideoAsset>>, t: Throwable) {
                if (!call.isCanceled) {
                    callback.onError()
                }
            }
        })
    }
}