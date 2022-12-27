package com.github.fernandospr.movies.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.fernandospr.movies.common.BaseViewModel
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.main.repository.MainRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class MainViewModel : BaseViewModel() {
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val error: MutableLiveData<Boolean> = MutableLiveData()
    private val results: MutableLiveData<Container<Show>?> = MutableLiveData()

    init {
        loading.value = false
        error.value = false
        results.value = null
    }

    fun getLoading(): LiveData<Boolean> = this.loading

    fun getError(): LiveData<Boolean> = this.error

    fun getItems(forceRefresh: Boolean = false): LiveData<Container<Show>?> {
        if (forceRefresh || (results.value == null && loading.value == false)) {
            loadItems()
        }
        return results
    }

    private fun loadItems() {
        loading.value = true
        error.value = false
        results.value = null

        doLoadItems(page = 1)
    }

    fun getNextPageItems() {
        results.value?.let { currentShows ->
            if (currentShows.page < currentShows.totalPages) {
                doLoadItems(currentShows.page + 1)
            }
        }
    }

    private fun doLoadItems(page: Int) {
        disposable.add(
            doLoadItemsFromRepository(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { new ->
                        loading.value = false
                        results.value = if (new.page > 1) {
                            new.copy(
                                results = (results.value?.results ?: emptyList()) + new.results
                            )
                        } else {
                            new
                        }
                    },
                    {
                        loading.value = false
                        error.value = true
                    }
                )
        )
    }

    abstract fun doLoadItemsFromRepository(page: Int): Single<Container<Show>>
}

class PopularMoviesViewModel(private val repo: MainRepository) : MainViewModel() {
    override fun doLoadItemsFromRepository(page: Int) = repo.loadPopularMovies(page)
}

class PopularTvShowsViewModel(private val repo: MainRepository) : MainViewModel() {
    override fun doLoadItemsFromRepository(page: Int) = repo.loadPopularTvShows(page)
}

class TopRatedMoviesViewModel(private val repo: MainRepository) : MainViewModel() {
    override fun doLoadItemsFromRepository(page: Int) = repo.loadTopRatedMovies(page)
}

class TopRatedTvShowsViewModel(private val repo: MainRepository) : MainViewModel() {
    override fun doLoadItemsFromRepository(page: Int) = repo.loadTopRatedTvShows(page)
}

class UpcomingMoviesViewModel(private val repo: MainRepository) : MainViewModel() {
    override fun doLoadItemsFromRepository(page: Int) = repo.loadUpcomingMovies(page)
}