package com.github.fernandospr.movies.common.di

import com.github.fernandospr.movies.BuildConfig
import com.github.fernandospr.movies.common.repository.network.Network
import com.github.fernandospr.movies.common.repository.network.NetworkImpl
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val WRITE_TIMEOUT = 10L
private const val READ_TIMEOUT = 30L
private const val CONNECT_TIMEOUT = 10L

val networkModule = module {
    single<Network> { NetworkImpl(get()) }

    single<Retrofit> {
        Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(get<OkHttpClient>())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(get<GsonConverterFactory>())
                .build()
    }

    single<Interceptor>(named("loggingInterceptor")) {
        HttpLoggingInterceptor().apply { level = BuildConfig.HTTP_LOGGING }
    }

    single<Interceptor>(named("apiKeyInterceptor")) {
        Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()

            val request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }
    }

    single<OkHttpClient> {
        val apiKeyInterceptor: Interceptor = get(named("apiKeyInterceptor"))
        val loggingInterceptor: Interceptor = get(named("loggingInterceptor"))
        OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(loggingInterceptor)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build()
    }

    single<GsonConverterFactory> {
        GsonConverterFactory.create(
                GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
        )
    }
}