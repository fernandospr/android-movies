package com.github.fernandospr.movies.detail.repository

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.models.VideoAsset
import io.reactivex.Single

interface DetailRepository {
    fun loadVideos(
        item: Show
    ): Single<Container<VideoAsset>>
}