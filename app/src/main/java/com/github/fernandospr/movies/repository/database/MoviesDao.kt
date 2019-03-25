package com.github.fernandospr.movies.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.fernandospr.movies.repository.Show
import io.reactivex.Observable

@Dao
interface MoviesDao {

    @Query("SELECT * FROM shows WHERE mediaType = :media AND categoryType = :category")
    fun getItemsByMediaAndCategory(media: String, category: String): Observable<List<Show>>

    @Query("SELECT * FROM shows WHERE UPPER(title) LIKE '%' || UPPER(:arg) || '%'")
    fun getItemsLike(arg: String): Observable<List<Show>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Show>)
}