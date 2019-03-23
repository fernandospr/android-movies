package com.github.fernandospr.movies

import com.github.fernandospr.movies.main.*
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryImpl
import com.github.fernandospr.movies.repository.network.MoviesApi
import com.github.fernandospr.movies.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<Repository> { RepositoryImpl(MoviesApi.create()) }

    viewModel { PopularMoviesViewModel(get()) }
    viewModel { PopularTvShowsViewModel(get()) }
    viewModel { TopRatedMoviesViewModel(get()) }
    viewModel { TopRatedTvShowsViewModel(get()) }
    viewModel { UpcomingMoviesViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}