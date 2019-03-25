package com.github.fernandospr.movies.repository.network

import com.github.fernandospr.movies.BuildConfig
import com.github.fernandospr.movies.repository.ApiConfigurationContainer
import com.github.fernandospr.movies.repository.Container
import com.github.fernandospr.movies.repository.Show
import com.github.fernandospr.movies.repository.VideoAsset
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

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

    companion object Factory {

        fun create(): MoviesApi {
            val httpClient = getHttpClient()

            val gsonConverterFactory = getGsonConverterFactory()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(httpClient)
                .build()

            return retrofit.create(MoviesApi::class.java)
        }

        private fun getHttpClient(): OkHttpClient {
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)

            httpClient
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(getApiKeyInterceptor())

            return httpClient.build()
        }

        private fun getLoggingInterceptor(): Interceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = BuildConfig.RETROFIT_LOGGING
            return interceptor
        }

        private fun getApiKeyInterceptor(): Interceptor = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()

            val request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }

        private fun getGsonConverterFactory(): Converter.Factory {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            return GsonConverterFactory.create(gson)
        }
    }
}
