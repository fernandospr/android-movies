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
    private lateinit var service: MainApi
    private lateinit var dao: MoviesDao
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
            "2019-01-01"
        )
        itemsContainer = Container(1, 2, listOf(item))

        service = mock()
        whenever(service.getPopularMovies(any())).thenReturn(mock())

        dao = mock()
        whenever(dao.getItemsByMediaAndCategory(any(), any())).thenReturn(mock())

        networkUtils = mock()

        repo = MainRepositoryImpl(service, dao, networkUtils)
    }

    @Test
    fun loadPopularMovies_shouldCallService_whenIsConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(service).getPopularMovies(any())
    }

    @Test
    fun loadPopularMovies_shouldNotCallService_whenIsNotConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.loadPopularMovies(1).subscribe()

        verify(service, never()).getPopularMovies(any())
    }

    @Test
    fun loadPopularMovies_shouldCallDao_whenIsNotConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.loadPopularMovies(1).subscribe()

        verify(dao).getItemsByMediaAndCategory(any(), any())
    }

    @Test
    fun loadPopularMovies_shouldNotCallDao_whenIsConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(dao, never()).getItemsByMediaAndCategory(any(), any())
    }

    @Test
    fun loadPopularMovies_shouldUpdateMediaCategoryAndSaveInDB() {
        whenever(service.getPopularMovies(any())).thenReturn(Single.just(itemsContainer))
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(dao).insertAll(itemsContainer.results.map { it.copy(mediaType = Show.MOVIE_TYPE, categoryType = Show.POPULAR_TYPE) })
    }

    @Test
    fun loadPopularMovies_shouldNotUpdateMediaAndCategory_whenIsConnectedToInternetAndServiceHasError() {
        whenever(service.getPopularMovies(any())).thenReturn(Single.error(Throwable()))
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1).subscribe()

        verify(dao, never()).insertAll(itemsContainer.results.map { it.copy(mediaType = Show.MOVIE_TYPE, categoryType = Show.POPULAR_TYPE) })
    }
}
