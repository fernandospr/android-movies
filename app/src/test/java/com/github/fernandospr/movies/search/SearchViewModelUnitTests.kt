package com.github.fernandospr.movies.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.github.fernandospr.movies.RxSchedulerRule
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class SearchViewModelUnitTests {

    @get:Rule
    val rxRule = RxSchedulerRule()

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var itemsContainer: Container<Show>
    private lateinit var repo: Repository
    private lateinit var viewModel: SearchViewModel
    private lateinit var loadingObserver: Observer<Boolean>
    private lateinit var errorObserver: Observer<Boolean>
    private lateinit var resultsObserver: Observer<Container<Show>?>

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
        whenever(repo.search(any(), any())).thenReturn(Single.just(itemsContainer))

        viewModel.search("jurassic")

        verify(loadingObserver).onChanged(eq(true))
    }

    @Test
    fun search_shouldEmitError_whenRepositoryReturnsError() {
        whenever(repo.search(any(), any())).thenReturn(Single.error(Throwable()))

        viewModel.search("jurassic")

        verify(errorObserver).onChanged(eq(true))
    }

    @Test
    fun search_shouldEmitItems_whenRepositoryReturnsSuccess() {
        whenever(repo.search(any(), any())).thenReturn(Single.just(itemsContainer))

        viewModel.search("jurassic")

        verify(resultsObserver).onChanged(eq(itemsContainer))
    }

    @Test
    fun search_shouldNotEmitItems_whenRepositoryReturnsError() {
        whenever(repo.search(any(), any())).thenReturn(Single.error(Throwable()))

        viewModel.search("jurassic")

        verify(resultsObserver, never()).onChanged(eq(itemsContainer))
    }

    @Test
    fun search_shouldNotEmitError_whenRepositoryReturnsSuccess() {
        whenever(repo.search(any(), any())).thenReturn(Single.just(itemsContainer))

        viewModel.search("jurassic")

        verify(errorObserver, never()).onChanged(eq(true))
    }

    @Test
    fun search_shouldEmitLoading_thenLoadFromRepository_thenEmitItems() {
        whenever(repo.search(any(), any())).thenReturn(Single.just(itemsContainer))

        viewModel.search("jurassic")

        inOrder(loadingObserver, resultsObserver, repo) {
            verify(loadingObserver).onChanged(eq(true))
            verify(repo).search(eq("jurassic"), eq(1))
            verify(resultsObserver).onChanged(eq(itemsContainer))
        }
    }

    @Test
    fun getNextPageItems_shouldLoadNextPageFromRepository() {
        whenever(repo.search(any(), any())).thenReturn(Single.just(itemsContainer))
        viewModel.search("jurassic")

        viewModel.getNextPageItems()

        verify(repo).search(eq("jurassic"), eq(2))
    }
}
