package com.github.fernandospr.movies.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.repository.network.ApiSearchResult
import com.github.fernandospr.movies.repository.network.ApiSearchResultsContainer
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity(), SearchAdapter.Listener {

    companion object {
        fun newIntent(context: Context) = Intent(context, SearchActivity::class.java)
    }

    private lateinit var adapter: SearchAdapter
    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSearchView()

        adapter = SearchAdapter()
        adapter.setListener(this)
        resultsContainer.adapter = adapter

        searchViewModel.getLoading().observe(this, Observer { isLoading ->
            isLoading?.let { showLoadingView(it) }
        })
        searchViewModel.getError().observe(this, Observer { error ->
            error?.let { showErrorView(it) }
        })
        searchViewModel.getResults().observe(this, Observer { entities ->
            showResult(entities)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupSearchView() {
        searchView.queryHint = getString(R.string.action_search)
        searchView.isFocusable = true
        searchView.setIconifiedByDefault(false)
        searchView.requestFocusFromTouch()
        searchView.requestFocus()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?) = false

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchViewModel.search(query)
                }
                searchView.clearFocus()
                return true
            }
        })
    }

    private fun showResult(container: ApiSearchResultsContainer?) {
        when {
            container == null -> {
                resultsContainer.visibility = View.INVISIBLE
                noResultsContainer.visibility = View.INVISIBLE
            }
            container.results.isEmpty() -> {
                resultsContainer.visibility = View.INVISIBLE
                noResultsContainer.visibility = View.VISIBLE
            }
            else -> {
                resultsContainer.visibility = View.VISIBLE
                noResultsContainer.visibility = View.INVISIBLE
                adapter.setEntities(container.results)
            }
        }
    }

    private fun showErrorView(show: Boolean) {
        if (show) {
            errorContainer.visibility = View.VISIBLE
        } else {
            errorContainer.visibility = View.INVISIBLE
        }
    }

    private fun showLoadingView(show: Boolean) {
        if (show) {
            loadingContainer.visibility = View.VISIBLE
        } else {
            loadingContainer.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(item: ApiSearchResult) {
        // FIXME
    }
}
