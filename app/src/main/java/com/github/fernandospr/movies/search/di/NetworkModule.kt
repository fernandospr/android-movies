package com.github.fernandospr.movies.search.di

import com.github.fernandospr.movies.search.repository.network.SearchApi
import org.koin.dsl.module
import retrofit2.Retrofit

val searchNetworkModule = module {
    single<SearchApi> {
        get<Retrofit>().create(SearchApi::class.java)
    }
}