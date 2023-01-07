package com.github.fernandospr.movies.main.di

import com.github.fernandospr.movies.common.repository.database.MoviesDatabase
import com.github.fernandospr.movies.common.repository.network.Network
import com.github.fernandospr.movies.main.*
import com.github.fernandospr.movies.main.repository.MainRepository
import com.github.fernandospr.movies.main.repository.MainRepositoryImpl
import com.github.fernandospr.movies.main.repository.network.MainApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single<MainRepository> {
        MainRepositoryImpl(
            get<MainApi>(),
            get<MoviesDatabase>().getMoviesDao(),
            get<Network>()
        )
    }

    viewModel { PopularMoviesViewModel(get()) }
    viewModel { PopularTvShowsViewModel(get()) }
    viewModel { TopRatedMoviesViewModel(get()) }
    viewModel { TopRatedTvShowsViewModel(get()) }
    viewModel { UpcomingMoviesViewModel(get()) }
}