package com.github.fernandospr.movies.main.di

import com.github.fernandospr.movies.main.repository.network.MainApi
import org.koin.dsl.module
import retrofit2.Retrofit

val mainNetworkModule = module {
    single<MainApi> {
        get<Retrofit>().create(MainApi::class.java)
    }
}