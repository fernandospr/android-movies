package com.github.fernandospr.movies.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.Show
import com.github.fernandospr.movies.repository.Container
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryCallback

abstract class MainViewModel : ViewModel() {
    protected val loading: MutableLiveData<Boolean> = MutableLiveData()
    protected val error: MutableLiveData<Boolean> = MutableLiveData()
    protected val results: MutableLiveData<Container<Show>> = MutableLiveData()

    val repoCallback = object : RepositoryCallback<Container<Show>> {
        override fun onSuccess(t: Container<Show>) {
            loading.value = false
            results.value?.let {
                t.results = it.results + t.results
            }
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

    fun getItems(): LiveData<Container<Show>> {
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

    abstract fun doLoadItems(page: Int = 1)

    fun getNextPageItems() {
        results.value?.let {
            if (it.page < it.totalPages) {
                doLoadItems(it.page + 1)
            }
        }
    }
}

class PopularMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadPopularMovies(page, repoCallback)
    }
}

class PopularTvShowsViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadPopularTvShows(page, repoCallback)
    }
}

class TopRatedMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadTopRatedMovies(page, repoCallback)
    }
}

class TopRatedTvShowsViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadTopRatedTvShows(page, repoCallback)
    }
}

class UpcomingMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadUpcomingMovies(page, repoCallback)
    }
}