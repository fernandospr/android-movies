package com.github.fernandospr.movies.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.fernandospr.movies.repository.ApiItem

@Database(entities = [(ApiItem::class)], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}