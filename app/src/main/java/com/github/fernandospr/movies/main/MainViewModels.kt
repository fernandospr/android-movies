package com.github.fernandospr.movies.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.Container
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryCallback
import com.github.fernandospr.movies.repository.Show

abstract class MainViewModel : ViewModel() {
    protected val loading: MutableLiveData<Boolean> = MutableLiveData()
    protected val error: MutableLiveData<Boolean> = MutableLiveData()
    protected val results: MutableLiveData<Container<Show>> = MutableLiveData()

    init {
        loading.value = false
        error.value = false
        results.value = null
    }

    fun getLoading(): LiveData<Boolean> = this.loading

    fun getError(): LiveData<Boolean> = this.error

    fun getItems(forceRefresh : Boolean = false): LiveData<Container<Show>> {
        if (forceRefresh || (results.value == null && loading.value == false)) {
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

    protected fun buildRepositoryCallback(page: Int): RepositoryCallback<Container<Show>> {
        return object : RepositoryCallback<Container<Show>> {
            override fun onSuccess(t: Container<Show>) {
                loading.value = false
                results.value?.let {
                    t.results = it.results + t.results
                }
                results.value = t
            }

            override fun onError() {
                if (page > 1) {
                    // TODO
                } else {
                    loading.value = false
                    error.value = true
                }
            }
        }
    }
}

class PopularMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadPopularMovies(page, buildRepositoryCallback(page))
    }
}

class PopularTvShowsViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadPopularTvShows(page, buildRepositoryCallback(page))
    }
}

class TopRatedMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadTopRatedMovies(page, buildRepositoryCallback(page))
    }
}

class TopRatedTvShowsViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadTopRatedTvShows(page, buildRepositoryCallback(page))
    }
}

class UpcomingMoviesViewModel(private val repo: Repository) : MainViewModel() {
    override fun doLoadItems(page: Int) {
        repo.loadUpcomingMovies(page, buildRepositoryCallback(page))
    }
}