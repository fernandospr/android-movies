package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.database.MoviesDao
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.Show.Companion.MOVIE_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.POPULAR_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.TOPRATED_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.TVSHOW_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.UPCOMING_TYPE
import com.github.fernandospr.movies.repository.models.Show.Companion.YOUTUBE_TYPE
import com.github.fernandospr.movies.repository.models.VideoAsset
import com.github.fernandospr.movies.repository.network.MoviesApi
import com.github.fernandospr.movies.repository.network.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

// TODO: Fetch configuration first
class RepositoryImpl(
    private val service: MoviesApi,
    private val dao: MoviesDao,
    private val networkUtils: NetworkUtils,
    private val diskIOExecutor: Executor) : Repository {

    private var popularMoviesObservable: DisposableObserver<List<Show>>? = null
    private var popularMoviesCall: Call<Container<Show>>? = null
    private var popularTvShowsObservable: DisposableObserver<List<Show>>? = null
    private var popularTvShowsCall: Call<Container<Show>>? = null
    private var topRatedMoviesObservable: DisposableObserver<List<Show>>? = null
    private var topRatedMoviesCall: Call<Container<Show>>? = null
    private var topRatedTvShowsObservable: DisposableObserver<List<Show>>? = null
    private var topRatedTvShowsCall: Call<Container<Show>>? = null
    private var upcomingMoviesObservable: DisposableObserver<List<Show>>? = null
    private var upcomingMoviesCall: Call<Container<Show>>? = null
    private var searchObservable: DisposableObserver<List<Show>>? = null
    private var searchCall: Call<Container<Show>>? = null
    private var videosCall: Call<Container<VideoAsset>>? = null

    override fun search(
            query: String,
            page: Int,
            callback: RepositoryCallback<Container<Show>>
    ) {
        if (networkUtils.isConnectedToInternet()) {
            searchCall = service.search(query, page)
            enqueueItemsContainerCall(searchCall!!, null, null, callback)
        } else {
            searchObservable = dao.getItemsLike(query)
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
            popularMoviesCall = service.getPopularMovies(page)
            enqueueItemsContainerCall(popularMoviesCall!!, MOVIE_TYPE, POPULAR_TYPE, callback)
        } else {
            popularMoviesObservable = dao.getItemsByMediaAndCategory(MOVIE_TYPE, POPULAR_TYPE)
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
            popularTvShowsCall = service.getPopularTvShows(page)
            enqueueItemsContainerCall(popularTvShowsCall!!, TVSHOW_TYPE, POPULAR_TYPE, callback)
        } else {
            popularTvShowsObservable = dao.getItemsByMediaAndCategory(TVSHOW_TYPE, POPULAR_TYPE)
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
            topRatedMoviesCall = service.getTopRatedMovies(page)
            enqueueItemsContainerCall(topRatedMoviesCall!!, MOVIE_TYPE, TOPRATED_TYPE, callback)
        } else {
            topRatedMoviesObservable = dao.getItemsByMediaAndCategory(MOVIE_TYPE, TOPRATED_TYPE)
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
            topRatedTvShowsCall = service.getTopRatedTvShows(page)
            enqueueItemsContainerCall(topRatedTvShowsCall!!, TVSHOW_TYPE, TOPRATED_TYPE, callback)
        } else {
            topRatedTvShowsObservable = dao.getItemsByMediaAndCategory(TVSHOW_TYPE, TOPRATED_TYPE)
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
            upcomingMoviesCall = service.getUpcomingMovies(page)
            enqueueItemsContainerCall(upcomingMoviesCall!!, MOVIE_TYPE, UPCOMING_TYPE, callback)
        } else {
            upcomingMoviesObservable = dao.getItemsByMediaAndCategory(MOVIE_TYPE, UPCOMING_TYPE)
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
                callback.onSuccess(Container(1, 1, value))
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
        videosCall =
                if (MOVIE_TYPE.equals(item.mediaType, true))
                    service.getMovieVideos(item.id)
                else
                    service.getTvShowVideos(item.id)

        videosCall?.enqueue(object : Callback<Container<VideoAsset>> {
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

    override fun stopSearch() {
        searchCall?.cancel()
        searchObservable?.dispose()
    }

    override fun stopPopularMovies() {
        popularMoviesCall?.cancel()
        popularMoviesObservable?.dispose()
    }

    override fun stopPopularTvShows() {
        popularTvShowsCall?.cancel()
        popularTvShowsObservable?.dispose()
    }

    override fun stopTopRatedMovies() {
        topRatedMoviesCall?.cancel()
        topRatedMoviesObservable?.dispose()
    }

    override fun stopTopRatedTvShows() {
        topRatedTvShowsCall?.cancel()
        topRatedTvShowsObservable?.dispose()
    }

    override fun stopUpcomingMovies() {
        upcomingMoviesCall?.cancel()
        upcomingMoviesObservable?.dispose()
    }

    override fun stopVideos() {
        videosCall?.cancel()
    }
}