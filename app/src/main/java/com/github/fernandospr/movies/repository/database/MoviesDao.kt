package com.github.fernandospr.movies.repository.database

import androidx.room.*
import com.github.fernandospr.movies.repository.ApiItem
import io.reactivex.Observable

@Dao
interface MoviesDao {

    @Query("SELECT * FROM items WHERE mediaType = :media AND categoryType = :category")
    fun getItemsByMediaAndCategory(media: String, category: String): Observable<List<ApiItem>>

    @Query("SELECT * FROM items WHERE UPPER(title) LIKE '%' || UPPER(:arg) || '%'")
    fun getItemsLike(arg: String): Observable<List<ApiItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<ApiItem>)
}