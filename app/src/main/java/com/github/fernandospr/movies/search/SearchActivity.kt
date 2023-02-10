package com.github.fernandospr.movies.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.EndlessRecyclerViewScrollListener
import com.github.fernandospr.movies.common.ItemAdapter
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.setVisible
import com.github.fernandospr.movies.databinding.ActivitySearchBinding
import com.github.fernandospr.movies.detail.DetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity(), ItemAdapter.Listener {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchAdapter = SearchAdapter().apply {
            setListener(this@SearchActivity)
        }
        setupSearchView(searchAdapter)
        setupResultsContainer(searchAdapter)
        setupErrorContainer()
        bindViewModel(searchAdapter)
    }

    private fun bindViewModel(searchAdapter: SearchAdapter) {
        viewModel.getLoading().observe(this) {
            binding.loadingContainer.root.setVisible(it)
        }
        viewModel.getError().observe(this) {
            binding.errorContainer.root.setVisible(it)
        }
        viewModel.getResults().observe(this) {
            showItems(it, searchAdapter)
        }
    }

    private fun setupErrorContainer() {
        binding.errorContainer.retryButton.setOnClickListener {
            viewModel.getResults()
        }
    }

    private fun setupResultsContainer(
        searchAdapter: SearchAdapter
    ) = with(binding.resultsContainer) {
        adapter = searchAdapter
        val scrollListener = object : EndlessRecyclerViewScrollListener(
            layoutManager as LinearLayoutManager
        ) {
            override fun onLoadMore(
                page: Int,
                totalItemsCount: Int,
                view: RecyclerView?
            ) {
                viewModel.getNextPageItems()
            }
        }
        addOnScrollListener(scrollListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        ActivityCompat.finishAfterTransition(this)
        return true
    }

    private fun setupSearchView(searchAdapter: SearchAdapter) = with(binding.searchView) {
        queryHint = getString(R.string.action_search)
        isFocusable = true
        setIconifiedByDefault(false)
        requestFocusFromTouch()
        requestFocus()
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?) = false

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchAdapter.clearEntities()
                    viewModel.search(query)
                }
                clearFocus()
                return true
            }
        })
    }

    private fun showItems(items: Container<Show>?, searchAdapter: SearchAdapter) = with(binding) {
        when {
            items == null -> {
                resultsContainer.setVisible(false)
                noresultsContainer.root.setVisible(false)
            }
            items.results.isEmpty() -> {
                resultsContainer.setVisible(false)
                noresultsContainer.root.setVisible(true)
            }
            else -> {
                resultsContainer.setVisible(true)
                noresultsContainer.root.setVisible(false)
                if (items.page == 1) {
                    searchAdapter.clearEntities()
                }
                searchAdapter.setEntities(items.results)
            }
        }
    }

    // region ItemAdapter.Listener methods
    override fun onItemClick(view: View, item: Show) {
        val intent = DetailActivity.newIntent(this, item)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            view,
            view.transitionName
        )
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }
    // endregion

    companion object {
        fun newIntent(context: Context) = Intent(context, SearchActivity::class.java)
    }
}
