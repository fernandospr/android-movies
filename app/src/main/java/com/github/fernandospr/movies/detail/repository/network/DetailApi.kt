package com.github.fernandospr.movies.detail.repository.network

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.VideoAsset
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface DetailApi {
    // https://developers.themoviedb.org/3/movies/get-movie-videos
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(@Path("movie_id") id: String): Single<Container<VideoAsset>>

    // https://developers.themoviedb.org/3/tv/get-tv-videos
    @GET("tv/{tv_id}/videos")
    fun getTvShowVideos(@Path("tv_id") id: String): Single<Container<VideoAsset>>
}
