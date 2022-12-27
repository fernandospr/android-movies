package com.github.fernandospr.movies.common.di

import androidx.room.Room
import com.github.fernandospr.movies.common.repository.database.MoviesDatabase
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
                get(),
                MoviesDatabase::class.java,
                "movies-master-db"
        ).build()
    }
}