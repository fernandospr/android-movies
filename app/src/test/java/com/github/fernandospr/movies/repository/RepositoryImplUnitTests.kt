package com.github.fernandospr.movies.repository

import com.github.fernandospr.movies.repository.database.MoviesDao
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.network.MoviesApi
import com.github.fernandospr.movies.repository.network.NetworkUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class RepositoryImplUnitTests {

    private lateinit var itemsContainer: Container<Show>
    private lateinit var repo: Repository
    private lateinit var service: MoviesApi
    private lateinit var serviceCall: Call<Container<Show>>
    private lateinit var dao: MoviesDao
    private lateinit var networkUtils: NetworkUtils
    private lateinit var repoCallback: RepositoryCallback<Container<Show>>

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

        serviceCall = mock()
        whenever(service.getPopularMovies(any())).thenReturn(serviceCall)

        dao = mock()
        whenever(dao.getItemsByMediaAndCategory(any(), any())).thenReturn(mock())
        whenever(dao.getItemsLike(any())).thenReturn(mock())

        networkUtils = mock()

        repo = RepositoryImpl(service, dao, networkUtils, CurrentThreadExecutor())
        repoCallback = mock()
    }

    inner class CurrentThreadExecutor : Executor {
        override fun execute(r: Runnable) = r.run()
    }

    @Test
    fun loadPopularMovies_shouldCallService_whenIsConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1, repoCallback)

        verify(service).getPopularMovies(any())
    }

    @Test
    fun loadPopularMovies_shouldNotCallService_whenIsNotConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.loadPopularMovies(1, repoCallback)

        verify(service, never()).getPopularMovies(any())
    }

    @Test
    fun loadPopularMovies_shouldCallDao_whenIsNotConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.loadPopularMovies(1, repoCallback)

        verify(dao).getItemsByMediaAndCategory(any(), any())
    }

    @Test
    fun loadPopularMovies_shouldNotCallDao_whenIsConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1, repoCallback)

        verify(dao, never()).getItemsByMediaAndCategory(any(), any())
    }

    @Test
    fun search_shouldCallDao_whenIsNotConnectedToInternet() {
        whenever(networkUtils.isConnectedToInternet()).thenReturn(false)

        repo.search("jurassic", 1, repoCallback)

        verify(dao).getItemsLike(eq("jurassic"))
    }

    @Test
    fun loadPopularMovies_shouldSaveInDB() {
        setupServiceCallWithItems()
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1, repoCallback)

        verify(dao).insertAll(itemsContainer.results)
    }

    @Test
    fun loadPopularMovies_shouldUpdateMediaAndCategory_whenIsConnectedToInternet() {
        setupServiceCallWithItems()
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1, repoCallback)

        itemsContainer.results.forEach {
            Assert.assertEquals(Show.POPULAR_TYPE, it.categoryType)
            Assert.assertEquals(Show.MOVIE_TYPE, it.mediaType)
        }
    }

    @Test
    fun loadPopularMovies_shouldNotUpdateMediaAndCategory_whenIsConnectedToInternetAndServiceHasError() {
        setupServiceCallWithError()
        whenever(networkUtils.isConnectedToInternet()).thenReturn(true)

        repo.loadPopularMovies(1, repoCallback)

        itemsContainer.results.forEach {
            Assert.assertNull(it.categoryType)
            Assert.assertNull(it.mediaType)
        }
    }

    private fun setupServiceCallWithItems() {
        doAnswer {
            val callback: Callback<Container<Show>> = it.getArgument(0)
            callback.onResponse(serviceCall, Response.success(itemsContainer))
        }.whenever(serviceCall).enqueue(any())
    }

    private fun setupServiceCallWithError() {
        doAnswer {
            val callback: Callback<Container<Show>> = it.getArgument(0)
            callback.onFailure(serviceCall, Throwable())
        }.whenever(serviceCall).enqueue(any())
    }
}
