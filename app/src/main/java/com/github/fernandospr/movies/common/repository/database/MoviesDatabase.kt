package com.github.fernandospr.movies.common.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.fernandospr.movies.common.repository.models.Show

/**
 * To enable DB logs execute:
 * adb shell setprop log.tag.SQLiteStatements VERBOSE
 *
 * Restart your app, use Logcat and filter by SQLiteStatements.
 *
 *
 * To disable DB logs execute:
 * adb shell setprop log.tag.SQLiteStatements \"\"
 *
 * Restart your app.
 */
@Database(entities = [(Show::class)], version = 2, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}