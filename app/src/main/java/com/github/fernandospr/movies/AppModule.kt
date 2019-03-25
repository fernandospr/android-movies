package com.github.fernandospr.movies

import androidx.room.Room
import com.github.fernandospr.movies.detail.DetailViewModel
import com.github.fernandospr.movies.main.*
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryImpl
import com.github.fernandospr.movies.repository.database.MoviesDatabase
import com.github.fernandospr.movies.repository.network.MoviesApi
import com.github.fernandospr.movies.repository.network.NetworkUtils
import com.github.fernandospr.movies.repository.network.NetworkUtilsImpl
import com.github.fernandospr.movies.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

val appModule = module {

    single {
        Room.databaseBuilder(
            get(),
            MoviesDatabase::class.java,
            "movies-master-db"
        )
            .build()
    }

    single<NetworkUtils> {
        NetworkUtilsImpl(get())
    }

    single<Repository> {
        RepositoryImpl(
            MoviesApi.create(),
            get<MoviesDatabase>().getMoviesDao(),
            get<NetworkUtils>(),
            Executors.newSingleThreadExecutor())
    }

    viewModel { PopularMoviesViewModel(get()) }
    viewModel { PopularTvShowsViewModel(get()) }
    viewModel { TopRatedMoviesViewModel(get()) }
    viewModel { TopRatedTvShowsViewModel(get()) }
    viewModel { UpcomingMoviesViewModel(get()) }

    viewModel { DetailViewModel(get()) }

    viewModel { SearchViewModel(get()) }
}