package com.github.fernandospr.movies.repository

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ApiConfigurationContainer(val images: ApiConfigurationImages)

data class ApiConfigurationImages(val secureBaseUrl: String)

data class Container<T>(
    val page: Int,
    val totalPages: Int,
    var results: List<T>
)

@Entity(tableName = "shows")
@Parcelize
data class Show(
	var mediaType: String?,
    var categoryType: String?,

    @PrimaryKey
    val id: String,

    @SerializedName("title", alternate = ["name"])
    val title: String?,

    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,

    @SerializedName("release_date", alternate = ["first_air_date"])
    val releaseDate: String?
) : Parcelable {
    fun getPosterFullPath() : String? {
        if (posterPath.isNullOrBlank()) {
            return null
        }
        return "https://image.tmdb.org/t/p/w342$posterPath"
    }

    fun getBackdropFullPath(): String? {
        if (backdropPath.isNullOrBlank()) {
            return null
        }
        return "https://image.tmdb.org/t/p/w780$backdropPath"
    }

    companion object {
        const val MOVIE_TYPE = "movie"
        const val TVSHOW_TYPE = "tv"
        const val POPULAR_TYPE = "popular"
        const val TOPRATED_TYPE = "toprated"
        const val UPCOMING_TYPE = "upcoming"
        const val YOUTUBE_TYPE = "YouTube"
    }
}

data class VideoAsset(
    val site: String?,
	val key: String?,
	val name: String?
) {
    val youtubeVideoPath: String?
        get() {
            if (key.isNullOrBlank()) {
                return null
            }
            return "https://www.youtube.com/watch?v=$key"
        }
    val youtubeThumbnailPath: String?
        get() {
            if (key.isNullOrBlank()) {
                return null
            }
            return "https://img.youtube.com/vi/$key/sddefault.jpg"
        }
}