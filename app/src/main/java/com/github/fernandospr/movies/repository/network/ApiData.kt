package com.github.fernandospr.movies.repository.network

data class ApiConfigurationContainer(val images: ApiConfigurationImages)

data class ApiConfigurationImages(val secureBaseUrl: String)

// FIXME: Generify/Simplify classes

data class ApiMoviesContainer(
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    val results: List<ApiMovie>
)

data class ApiMovie(
    val id: String?,
    val title: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val releaseDate: String?
)

data class ApiTvShowsContainer(
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    val results: List<ApiTvShow>
)

data class ApiTvShow(
    val id: String?,
    val name: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val firstAirDate: String?
)

data class ApiSearchResultsContainer(
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    val results: List<ApiSearchResult>
)

data class ApiSearchResult(
	val type: String?,

    val id: String?,

    val title: String?,
    val name: String?,

    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,

    val releaseDate: String?,
    val firstAirDate: String?
) {
    fun getTitleOrName(): String? {
        if (title.isNullOrBlank()) {
            return name
        }
        return title
    }
}

data class ApiVideoContainer(
    val page: Int,
    val totalResults: Int,
	val totalPages: Int,
	val results: List<ApiVideoResult>
)

data class ApiVideoResult(
    val site: String?,
	val key: String?,
	val name: String?
)