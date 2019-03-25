package com.github.fernandospr.movies.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.fernandospr.movies.repository.Show

@Database(entities = [(Show::class)], version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}