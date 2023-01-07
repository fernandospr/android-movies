package com.github.fernandospr.movies.search.di

import com.github.fernandospr.movies.common.repository.database.MoviesDatabase
import com.github.fernandospr.movies.common.repository.network.Network
import com.github.fernandospr.movies.search.SearchViewModel
import com.github.fernandospr.movies.search.repository.SearchRepository
import com.github.fernandospr.movies.search.repository.SearchRepositoryImpl
import com.github.fernandospr.movies.search.repository.network.SearchApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            get<SearchApi>(),
            get<MoviesDatabase>().getMoviesDao(),
            get<Network>()
        )
    }

    viewModel { SearchViewModel(get()) }
}