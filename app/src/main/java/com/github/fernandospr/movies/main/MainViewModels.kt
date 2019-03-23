package com.github.fernandospr.movies.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryCallback
import com.github.fernandospr.movies.repository.network.ApiItemsContainer

abstract class MainViewModel : ViewModel() {
    protected val loading: MutableLiveData<Boolean> = MutableLiveData()
    protected val error: MutableLiveData<Boolean> = MutableLiveData()
    protected val results: MutableLiveData<ApiItemsContainer> = MutableLiveData()

    val repoCallback = object : RepositoryCallback<ApiItemsContainer> {
        override fun onSuccess(t: ApiItemsContainer) {
            loading.value = false
            results.value = t
        }

        override fun onError() {
            loading.value = false
            error.value = true
        }
    }

    init {
        loading.value = false
        error.value = false
        results.value = null
    }

    fun getLoading(): LiveData<Boolean> = this.loading

    fun getError(): LiveData<Boolean> = this.error

    fun getItems(): LiveData<ApiItemsContainer> {
        if (results.value == null && loading.value == false) {
            loadItems()
        }
        return results
    }

    private fun loadItems() {
        loading.value = true
        error.value = false
        results.value = null
        doLoadItems()
    }

    abstract fun doLoadItems()
}

class PopularMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems() {
        // FIXME: Pages
        repo.loadPopularMovies(1, repoCallback)
    }
}

class PopularTvShowsViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems() {
        // FIXME: Pages
        repo.loadPopularTvShows(1, repoCallback)
    }
}

class TopRatedMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems() {
        // FIXME: Pages
        repo.loadTopRatedMovies(1, repoCallback)
    }
}

class TopRatedTvShowsViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems() {
        // FIXME: Pages
        repo.loadTopRatedTvShows(1, repoCallback)
    }
}

class UpcomingMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems() {
        // FIXME: Pages
        repo.loadUpcomingMovies(1, repoCallback)
    }
}