package com.github.fernandospr.movies.common.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.fernandospr.movies.common.repository.models.Show
import io.reactivex.Single

@Dao
interface MoviesDao {

    @Query("SELECT * FROM shows WHERE mediaType = :media AND categoryType = :category")
    fun getItemsByMediaAndCategory(media: Show.Media, category: Show.Category): Single<List<Show>>

    @Query("SELECT * FROM shows WHERE UPPER(title) LIKE '%' || UPPER(:arg) || '%'")
    fun getItemsLike(arg: String): Single<List<Show>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Show>)
}