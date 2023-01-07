package com.github.fernandospr.movies.common.repository.models

data class VideoAsset(
    val site: String?,
	val key: String?,
	val name: String?
) {
    val youtubeVideoPath: String?
        get() {
            if (key.isNullOrBlank() || !Site.YOUTUBE.name.equals(site, true)) {
                return null
            }
            return "https://www.youtube.com/watch?v=$key"
        }
    val youtubeThumbnailPath: String?
        get() {
            if (key.isNullOrBlank() || !Site.YOUTUBE.name.equals(site, true)) {
                return null
            }
            return "https://img.youtube.com/vi/$key/sddefault.jpg"
        }

    enum class Site {
        YOUTUBE
    }
}