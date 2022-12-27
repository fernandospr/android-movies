package com.github.fernandospr.movies.common.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.fernandospr.movies.common.repository.models.Show

@Database(entities = [(Show::class)], version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}