package com.github.fernandospr.movies.detail.di

import com.github.fernandospr.movies.detail.repository.network.DetailApi
import org.koin.dsl.module
import retrofit2.Retrofit

val detailNetworkModule = module {
    single<DetailApi> {
        get<Retrofit>().create(DetailApi::class.java)
    }
}