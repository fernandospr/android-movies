package com.github.fernandospr.movies.main.repository

import com.github.fernandospr.movies.RxSchedulerRule
import com.github.fernandospr.movies.common.repository.database.MoviesDao
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import com.github.fernandospr.movies.main.repository.network.MainApi
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class MainRepositoryTests {

    @get:Rule
    val rxRule = RxSchedulerRule()

    private lateinit var itemsContainer: Container<Show>
    private lateinit var repo: MainRepository
    private lateinit var apiService: MainApi
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
        whenever(apiService.getPopularMovies(any())).thenReturn(mock())

        dbDao = mock()
        whenever(dbDao.getItemsByMediaAndCategory(any(), any())).thenReturn(mock())

        networkUtils = mock()

        repo = MainRepositoryImpl(apiService, dbDao, networkUtils)
    }

    @Test
    fun `Loading popular movies should call service when is connected to Internet`() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(apiService).getPopularMovies(any())
    }

    @Test
    fun `Loading popular movies should not call service when is not connected to Internet`() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.loadPopularMovies(1).subscribe()

        verify(apiService, never()).getPopularMovies(any())
    }

    @Test
    fun `Loading popular movies should call dao when is not connected to Internet`() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.loadPopularMovies(1).subscribe()

        verify(dbDao).getItemsByMediaAndCategory(any(), any())
    }

    @Test
    fun `Loading popular movies should not call dao when is connected to Internet`() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(dbDao, never()).getItemsByMediaAndCategory(any(), any())
    }

    @Test
    fun `Loading popular movies should update media and category types and save to database when is connected to internet`() {
        whenever(apiService.getPopularMovies(any())).thenReturn(Single.just(itemsContainer))
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(dbDao).insertAll(itemsContainer.results.map {
            it.copy(
                mediaType = Show.MOVIE_TYPE,
                categoryType = Show.POPULAR_TYPE
            )
        })
    }

    @Test
    fun `Loading popular movies should not save to database when is connected to internet but service returns error`() {
        val error = Throwable()
        whenever(apiService.getPopularMovies(any())).thenReturn(Single.error(error))
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        val testObserver = repo.loadPopularMovies(1).test()

        testObserver.assertError(error)
        verify(dbDao, never()).insertAll(any())
    }
}
