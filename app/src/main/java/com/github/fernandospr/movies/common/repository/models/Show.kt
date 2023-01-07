package com.github.fernandospr.movies.common.repository.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "shows")
@Parcelize
data class Show(
	var mediaType: Media?,
    var categoryType: Category?,

    @PrimaryKey
    val id: String,

    @SerializedName("title", alternate = ["name"])
    val title: String?,

    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val voteAverage: String?,

    @SerializedName("release_date", alternate = ["first_air_date"])
    val releaseDate: String?
) : Parcelable {
    val posterFullPath : String?
        get() {
            if (posterPath.isNullOrBlank()) {
                return null
            }
            return "https://image.tmdb.org/t/p/w342$posterPath"
        }

    val backdropFullPath: String?
        get() {
            if (backdropPath.isNullOrBlank()) {
                return null
            }
            return "https://image.tmdb.org/t/p/w780$backdropPath"
        }

    enum class Media {
        MOVIE, TV
    }

    enum class Category {
        POPULAR, TOPRATED, UPCOMING
    }
}
