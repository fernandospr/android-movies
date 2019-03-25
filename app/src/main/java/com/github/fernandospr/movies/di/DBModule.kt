package com.github.fernandospr.movies.di

import androidx.room.Room
import com.github.fernandospr.movies.repository.database.MoviesDatabase
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