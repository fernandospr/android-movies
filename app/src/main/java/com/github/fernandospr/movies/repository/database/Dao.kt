package com.github.fernandospr.movies.repository.database

import androidx.room.*
import com.github.fernandospr.movies.repository.network.ApiItem
import io.reactivex.Observable

@Database(entities = [(ApiItem::class)], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun getMoviesDao(): MoviesDao
}

@Dao
interface MoviesDao {

    @Query("SELECT * FROM items WHERE mediaType = :media AND categoryType = :category")
    fun getItemsByMediaAndCategory(media: String, category: String): Observable<List<ApiItem>>

    @Query("SELECT * FROM items WHERE UPPER(title) LIKE '%' || UPPER(:arg) || '%'")
    fun getItemsLike(arg: String): Observable<List<ApiItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<ApiItem>)
}