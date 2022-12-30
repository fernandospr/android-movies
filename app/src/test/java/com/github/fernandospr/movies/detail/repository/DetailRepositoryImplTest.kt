package com.github.fernandospr.movies.detail.repository

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.models.VideoAsset
import com.github.fernandospr.movies.common.repository.network.Network
import com.github.fernandospr.movies.detail.repository.network.DetailApi
import io.reactivex.Single
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class DetailRepositoryImplTest {

    private lateinit var item: Show
    private lateinit var repo: DetailRepository
    private lateinit var apiService: DetailApi
    private lateinit var network: Network

    @Before
    fun setup() {
        item = Show(
            Show.Media.MOVIE,
            Show.Category.POPULAR,
            "1",
            "Test1",
            null,
            null,
            "Overview text",
            "8.0",
            "2019-01-01"
        )
        val videosContainer = Container(
            page = 1,
            totalPages = 1,
            results = listOf(
                VideoAsset(site = VideoAsset.Site.YOUTUBE.name, key = "123", name = "test 1"),
                VideoAsset(site = VideoAsset.Site.YOUTUBE.name, key = "456", name = "test 2"),
                VideoAsset(site = "tiktok", key = "789", name = "test 3"),
            )
        )
        apiService = mock()
        network = mock()

        whenever(apiService.getMovieVideos(any())).thenReturn(Single.just(videosContainer))

        repo = DetailRepositoryImpl(apiService, network)
    }

    @Test
    fun `Loading videos for a movie should call movie service`() {
        whenever(network.isOnline()).thenReturn(true)

        repo.loadVideos(item).subscribe()

        verify(apiService).getMovieVideos(eq(item.id))
    }

    @Test
    fun `Loading videos for a movie should filter Youtube videos`() {
        whenever(network.isOnline()).thenReturn(true)

        val actualVideosContainer = repo.loadVideos(item).blockingGet()

        assertTrue(
            actualVideosContainer.results.all {
                VideoAsset.Site.YOUTUBE.name.equals(it.site, true)
            }
        )
    }

}