package com.github.fernandospr.movies.search.repository

import com.github.fernandospr.movies.RxSchedulerRule
import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import com.github.fernandospr.movies.search.repository.network.SearchApi
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class SearchRepositoryTests {

    @get:Rule
    val rxRule = RxSchedulerRule()

    private lateinit var itemsContainer: Container<Show>
    private lateinit var repo: SearchRepository
    private lateinit var apiService: SearchApi
    private lateinit var dbDao: MoviesDao
    private lateinit var networkUtils: NetworkUtils

    @Before
    fun setup() {
        val item = Show(
            null,
            null,
            "1",
            "Test1",
            null,
            null,
            "Overview text",
            "8.0",
            "2019-01-01"
        )
        itemsContainer = Container(1, 2, listOf(item))
        apiService = mock()
        dbDao = mock()
        networkUtils = mock()

        whenever(apiService.search(any(), any())).thenReturn(Single.just(itemsContainer))
        whenever(dbDao.getItemsLike(any())).thenReturn(mock())

        repo = SearchRepositoryImpl(apiService, dbDao, networkUtils)
    }

    @Test
    fun `Searching should call dao when is not connected to Internet`() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.search("jurassic", 1).subscribe()

        verify(dbDao).getItemsLike(eq("jurassic"))
    }

    @Test
    fun `Searching should call service and save to database when is connected to Internet`() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.search("jurassic", 1).subscribe()

        inOrder(apiService, dbDao) {
            verify(apiService).search(any(), any())
            verify(dbDao).insertAll(any())
        }
    }

    @Test
    fun `Searching should not save to database when is connected to Internet but service returns error`() {
        val error = Throwable()
        whenever(apiService.search(any(), any())).thenReturn(Single.error(error))
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        val testObserver = repo.search("jurassic", 1).test()

        testObserver.assertError(error)
        verify(dbDao, never()).insertAll(any())
    }
}
