package com.github.fernandospr.movies.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.fernandospr.movies.common.BaseViewModel
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.search.repository.SearchRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchViewModel(private val repo: SearchRepository) : BaseViewModel() {
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val error: MutableLiveData<Boolean> = MutableLiveData()
    private val results: MutableLiveData<Container<Show>?> = MutableLiveData()
    private var lastQuery: String = ""

    init {
        loading.value = false
        error.value = false
        results.value = null
    }

    fun getLoading(): LiveData<Boolean> = this.loading
    fun getError(): LiveData<Boolean> = this.error
    fun getResults(): LiveData<Container<Show>?> = this.results

    fun search(query: String) {
        loading.value = true
        error.value = false
        results.value = null

        doSearch(query, 1)
    }

    fun getNextPageItems() {
        results.value?.let {
            if (it.page < it.totalPages) {
                doSearch(lastQuery, it.page + 1)
            }
        }
    }

    private fun doSearch(query: String, page: Int) {
        disposable.add(
            repo.search(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { new ->
                        loading.value = false
                        results.value = if (lastQuery == query && new.page > 1) {
                            new.copy(
                                results = (results.value?.results ?: emptyList()) + new.results
                            )
                        } else {
                            new
                        }
                        lastQuery = query
                    },
                    {
                        loading.value = false
                        error.value = true
                    }
                )
        )
    }
}