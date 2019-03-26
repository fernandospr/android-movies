package com.github.fernandospr.movies.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryCallback
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

class SearchViewModelUnitTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var itemsContainer: Container<Show>
    private lateinit var repo: Repository
    private lateinit var viewModel: SearchViewModel
    private lateinit var loadingObserver: Observer<Boolean>
    private lateinit var errorObserver: Observer<Boolean>
    private lateinit var resultsObserver: Observer<Container<Show>>

    @Before
    fun setup() {
        val item = Show(
                "movie",
                "popular",
                "1",
                "Test1",
                null,
                null,
                "Overview text",
                "2019-01-01"
        )
        itemsContainer = Container(1, 2, listOf(item))
        repo = mock()
        viewModel = SearchViewModel(repo)

        loadingObserver = mock()
        errorObserver = mock()
        resultsObserver = mock()

        viewModel.getLoading().observeForever(loadingObserver)
        viewModel.getError().observeForever(errorObserver)
        viewModel.getResults().observeForever(resultsObserver)
    }

    @Test
    fun search_shouldEmitLoading() {
        setupRepositoryWithSuccess()

        viewModel.search("jurassic")

        verify(loadingObserver).onChanged(eq(true))
    }

    @Test
    fun search_shouldEmitError_whenRepositoryReturnsError() {
        setupRepositoryWithError()

        viewModel.search("jurassic")

        verify(errorObserver).onChanged(eq(true))
    }

    @Test
    fun search_shouldEmitItems_whenRepositoryReturnsSuccess() {
        setupRepositoryWithSuccess()

        viewModel.search("jurassic")

        verify(resultsObserver).onChanged(eq(itemsContainer))
    }

    @Test
    fun search_shouldNotEmitItems_whenRepositoryReturnsError() {
        setupRepositoryWithError()

        viewModel.search("jurassic")

        verify(resultsObserver, never()).onChanged(eq(itemsContainer))
    }

    @Test
    fun search_shouldNotEmitError_whenRepositoryReturnsSuccess() {
        setupRepositoryWithSuccess()

        viewModel.search("jurassic")

        verify(errorObserver, never()).onChanged(eq(true))
    }

    @Test
    fun search_shouldEmitLoading_thenLoadFromRepository_thenEmitItems() {
        setupRepositoryWithSuccess()

        viewModel.search("jurassic")

        inOrder(loadingObserver, resultsObserver, repo) {
            verify(loadingObserver).onChanged(eq(true))
            verify(repo).search(eq("jurassic"), eq(1), any())
            verify(resultsObserver).onChanged(eq(itemsContainer))
        }
    }

    @Test
    fun getNextPageItems_shouldLoadNextPageFromRepository() {
        setupRepositoryWithSuccess()

        viewModel.search("jurassic")
        viewModel.getNextPageItems()

        verify(repo).search(eq("jurassic"), eq(2), any())
    }

    private fun setupRepositoryWithSuccess() {
        doAnswer {
            val callback: RepositoryCallback<Container<Show>> = it.getArgument(2)
            callback.onSuccess(itemsContainer)
        }.whenever(repo).search(anyString(), anyInt(), any())
    }

    private fun setupRepositoryWithError() {
        doAnswer {
            val callback: RepositoryCallback<Container<Show>> = it.getArgument(2)
            callback.onError()
        }.whenever(repo).search(anyString(), anyInt(), any())
    }
}
