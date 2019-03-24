package com.github.fernandospr.movies.repository.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ApiConfigurationContainer(val images: ApiConfigurationImages)

data class ApiConfigurationImages(val secureBaseUrl: String)

data class ApiItemsContainer(
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    var results: List<ApiItem>
)

@Parcelize
data class ApiItem(
	var mediaType: String?,

    val id: String?,

    @SerializedName("title", alternate= ["name"])
    val title: String?,

    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,

    @SerializedName("release_date", alternate= ["first_air_date"])
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
}

data class ApiVideosContainer(
    val page: Int,
    val totalResults: Int,
	val totalPages: Int,
	val results: List<ApiVideoResult>
)

data class ApiVideoResult(
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