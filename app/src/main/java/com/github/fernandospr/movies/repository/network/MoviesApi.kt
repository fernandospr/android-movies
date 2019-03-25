package com.github.fernandospr.movies.repository.network

import com.github.fernandospr.movies.repository.ApiConfigurationContainer
import com.github.fernandospr.movies.repository.Container
import com.github.fernandospr.movies.repository.Show
import com.github.fernandospr.movies.repository.VideoAsset
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    // https://developers.themoviedb.org/3/configuration/get-api-configuration
    @GET("configuration")
    fun getConfiguration(): Call<ApiConfigurationContainer>

    // https://developers.themoviedb.org/3/movies/get-popular-movies
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Call<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-top-rated-movies
    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Call<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-upcoming
    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: Int): Call<Container<Show>>

    // https://developers.themoviedb.org/3/movies/get-movie-videos
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(@Path("movie_id") id: String): Call<Container<VideoAsset>>

    // https://developers.themoviedb.org/3/tv/get-popular-tv-shows
    @GET("tv/popular")
    fun getPopularTvShows(@Query("page") page: Int): Call<Container<Show>>

    // https://developers.themoviedb.org/3/tv/get-top-rated-tv
    @GET("tv/top_rated")
    fun getTopRatedTvShows(@Query("page") page: Int): Call<Container<Show>>

    // https://developers.themoviedb.org/3/tv/get-tv-videos
    @GET("tv/{tv_id}/videos")
    fun getTvShowVideos(@Path("tv_id") id: String): Call<Container<VideoAsset>>

    // https://developers.themoviedb.org/3/search/multi-search
    @GET("search/multi")
    fun search(@Query("query") query: String,
               @Query("page") page: Int): Call<Container<Show>>
}
