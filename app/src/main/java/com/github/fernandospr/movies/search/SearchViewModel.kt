package com.github.fernandospr.movies.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryCallback
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show

class SearchViewModel(private val repo: Repository) : ViewModel() {
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val error: MutableLiveData<Boolean> = MutableLiveData()
    private val results: MutableLiveData<Container<Show>> = MutableLiveData()
    private var lastQuery: String = ""

    init {
        loading.value = false
        error.value = false
        results.value = null
    }

    fun getLoading(): LiveData<Boolean> = this.loading
    fun getError(): LiveData<Boolean> = this.error
    fun getResults(): LiveData<Container<Show>> = this.results

    fun search(query: String) {
        loading.value = true
        error.value = false
        results.value = null

        doSearch(query, 1)
    }

    private fun doSearch(query: String, page: Int) {
        repo.search(query, page, object : RepositoryCallback<Container<Show>> {
            override fun onSuccess(t: Container<Show>) {
                loading.value = false
                if (lastQuery == query) {
                    results.value?.let {
                        t.results = it.results + t.results
                    }
                }
                results.value = t
                lastQuery = query
            }

            override fun onError() {
                if (page > 1) {
                    // TODO
                } else {
                    loading.value = false
                    error.value = true
                }
            }

        })
    }

    fun getNextPageItems() {
        results.value?.let {
            if (it.page < it.totalPages) {
                doSearch(lastQuery, it.page + 1)
            }
        }
    }

    override fun onCleared() {
        repo.stopSearch()
    }
}