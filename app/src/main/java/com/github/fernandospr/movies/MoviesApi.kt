package com.github.fernandospr.movies

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface MoviesApi {

    // FIXME

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

        private fun getGsonConverterFactory() : Converter.Factory {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            return GsonConverterFactory.create(gson)
        }
    }
}
