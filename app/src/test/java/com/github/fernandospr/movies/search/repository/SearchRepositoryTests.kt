package com.github.fernandospr.movies.search.repository

import com.github.fernandospr.movies.RxSchedulerRule
import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import com.github.fernandospr.movies.search.repository.network.SearchApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class SearchRepositoryTests {

    @get:Rule
    val rxRule = RxSchedulerRule()

    private lateinit var repo: SearchRepository
    private lateinit var service: SearchApi
    private lateinit var dao: MoviesDao
    private lateinit var networkUtils: NetworkUtils

    @Before
    fun setup() {
        service = mock()
        dao = mock()
        networkUtils = mock()

        whenever(service.search(any(), any())).thenReturn(mock())
        whenever(dao.getItemsLike(any())).thenReturn(mock())

        repo = SearchRepositoryImpl(service, dao, networkUtils)
    }

    @Test
    fun search_shouldCallService_whenIsConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.search("jurassic", 1).subscribe()

        verify(service).search(any(), any())
    }

    @Test
    fun search_shouldCallDao_whenIsNotConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.search("jurassic", 1).subscribe()

        verify(dao).getItemsLike(eq("jurassic"))
    }
}
