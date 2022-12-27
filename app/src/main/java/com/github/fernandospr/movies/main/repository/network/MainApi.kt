package com.github.fernandospr.movies.main.repository.network

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MainApi {

    // https://developers.themoviedb.org/3/movies/get-popular-movies
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-top-rated-movies
    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-upcoming
    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/tv/get-popular-tv-shows
    @GET("tv/popular")
    fun getPopularTvShows(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/tv/get-top-rated-tv
    @GET("tv/top_rated")
    fun getTopRatedTvShows(@Query("page") page: Int): Single<Container<Show>>
}
