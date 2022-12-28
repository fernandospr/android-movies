package com.github.fernandospr.movies.detail.repository

import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.models.Show.Companion.MOVIE_TYPE
import com.github.fernandospr.movies.common.repository.models.Show.Companion.YOUTUBE_TYPE
import com.github.fernandospr.movies.detail.repository.network.DetailApi

class DetailRepositoryImpl(
    private val service: DetailApi
) : DetailRepository {

    override fun loadVideos(
        item: Show
    ) = if (MOVIE_TYPE.equals(item.mediaType, true)) {
        service.getMovieVideos(item.id)
    } else {
        service.getTvShowVideos(item.id)
    }.doOnSuccess { videosContainer ->
        videosContainer.results.filter { YOUTUBE_TYPE.equals(it.site, true) }
    }
}