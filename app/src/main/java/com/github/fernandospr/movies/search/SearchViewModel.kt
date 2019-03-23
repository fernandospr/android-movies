package com.github.fernandospr.movies.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.RepositoryCallback
import com.github.fernandospr.movies.repository.network.ApiItemsContainer

class SearchViewModel(private val repo: Repository) : ViewModel() {
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val error: MutableLiveData<Boolean> = MutableLiveData()
    private val results: MutableLiveData<ApiItemsContainer> = MutableLiveData()

    init {
        loading.value = false
        error.value = false
        results.value = null
    }

    fun getLoading(): LiveData<Boolean> = this.loading
    fun getError(): LiveData<Boolean> = this.error
    fun getResults(): LiveData<ApiItemsContainer> = this.results

    fun search(query: String) {
        loading.value = true
        error.value = false
        results.value = null
        // FIXME: Pages
        repo.search(query, 1, object : RepositoryCallback<ApiItemsContainer> {
            override fun onSuccess(entities: ApiItemsContainer) {
                loading.value = false
                results.value = entities
            }

            override fun onError() {
                loading.value = false
                error.value = true
            }

        })
    }
}