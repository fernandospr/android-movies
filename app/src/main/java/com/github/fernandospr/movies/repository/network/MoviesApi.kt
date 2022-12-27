package com.github.fernandospr.movies.repository.network

import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.VideoAsset
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    // https://developers.themoviedb.org/3/movies/get-popular-movies
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-top-rated-movies
    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-upcoming
    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-movie-videos
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(@Path("movie_id") id: String): Single<Container<VideoAsset>>

    // https://developers.themoviedb.org/3/tv/get-popular-tv-shows
    @GET("tv/popular")
    fun getPopularTvShows(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/tv/get-top-rated-tv
    @GET("tv/top_rated")
    fun getTopRatedTvShows(@Query("page") page: Int): Single<Container<Show>>

    // https://developers.themoviedb.org/3/tv/get-tv-videos
    @GET("tv/{tv_id}/videos")
    fun getTvShowVideos(@Path("tv_id") id: String): Single<Container<VideoAsset>>

    // https://developers.themoviedb.org/3/search/multi-search
    @GET("search/multi")
    fun search(
        @Query("query") query: String,
        @Query("page") page: Int
    ): Single<Container<Show>>
}
