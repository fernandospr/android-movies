package com.github.fernandospr.movies.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.EndlessRecyclerViewScrollListener
import com.github.fernandospr.movies.common.ItemAdapter
import com.github.fernandospr.movies.detail.DetailActivity
import com.github.fernandospr.movies.repository.ApiItem
import com.github.fernandospr.movies.repository.ApiItemsContainer
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.category_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SearchActivity::class.java)
    }

    private lateinit var adapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSearchView()

        adapter = SearchAdapter()
        adapter.setListener(object : ItemAdapter.Listener {
            override fun onItemClick(view: View, item: ApiItem) {
                val intent = DetailActivity.newIntent(this@SearchActivity, item)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SearchActivity,
                        view,
                        ViewCompat.getTransitionName(view)!!)
                ActivityCompat.startActivity(this@SearchActivity, intent, options.toBundle())
            }
        })
        resultsContainer.adapter = adapter

        viewModel.getLoading().observe(this, Observer { isLoading ->
            isLoading?.let { showLoadingView(it) }
        })
        viewModel.getError().observe(this, Observer { error ->
            error?.let { showErrorView(it) }
        })
        viewModel.getResults().observe(this, Observer { entities ->
            showResult(entities)
        })
        errorContainer.retryButton.setOnClickListener {
            viewModel.getResults()
        }
        val scrollListener = object : EndlessRecyclerViewScrollListener(
                resultsContainer.layoutManager as LinearLayoutManager
        ) {
            override fun onLoadMore(page: Int,
                                    totalItemsCount: Int,
                                    view: RecyclerView?) {
                viewModel.getNextPageItems()
            }
        }
        resultsContainer.addOnScrollListener(scrollListener)
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
                    adapter.clearEntities()
                    viewModel.search(query)
                }
                searchView.clearFocus()
                return true
            }
        })
    }

    private fun showResult(container: ApiItemsContainer?) {
        when {
            container == null -> {
                resultsContainer.visibility = View.INVISIBLE
                noresultsContainer.visibility = View.INVISIBLE
            }
            container.results.isEmpty() -> {
                resultsContainer.visibility = View.INVISIBLE
                noresultsContainer.visibility = View.VISIBLE
            }
            else -> {
                resultsContainer.visibility = View.VISIBLE
                noresultsContainer.visibility = View.INVISIBLE
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
}
