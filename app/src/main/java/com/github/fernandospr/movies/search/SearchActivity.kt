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
import com.github.fernandospr.movies.databinding.ActivitySearchBinding
import com.github.fernandospr.movies.detail.DetailActivity
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    companion object {
        fun newIntent(context: Context) = Intent(context, SearchActivity::class.java)
    }

    private lateinit var adapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSearchView()

        adapter = SearchAdapter()
        adapter.setListener(object : ItemAdapter.Listener {
            override fun onItemClick(view: View, item: Show) {
                val intent = DetailActivity.newIntent(this@SearchActivity, item)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SearchActivity,
                        view,
                        ViewCompat.getTransitionName(view)!!)
                ActivityCompat.startActivity(this@SearchActivity, intent, options.toBundle())
            }
        })
        binding.resultsContainer.adapter = adapter

        viewModel.getLoading().observe(this, Observer { isLoading ->
            isLoading?.let { showLoadingView(it) }
        })
        viewModel.getError().observe(this, Observer { error ->
            error?.let { showErrorView(it) }
        })
        viewModel.getResults().observe(this, Observer { entities ->
            showResult(entities)
        })
        binding.errorContainer.retryButton.setOnClickListener {
            viewModel.getResults()
        }
        val scrollListener = object : EndlessRecyclerViewScrollListener(
            binding.resultsContainer.layoutManager as LinearLayoutManager
        ) {
            override fun onLoadMore(page: Int,
                                    totalItemsCount: Int,
                                    view: RecyclerView?) {
                viewModel.getNextPageItems()
            }
        }
        binding.resultsContainer.addOnScrollListener(scrollListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        ActivityCompat.finishAfterTransition(this)
        return true
    }

    private fun setupSearchView() = with(binding.searchView) {
        queryHint = getString(R.string.action_search)
        isFocusable = true
        setIconifiedByDefault(false)
        requestFocusFromTouch()
        requestFocus()
        setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?) = false

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    adapter.clearEntities()
                    viewModel.search(query)
                }
                clearFocus()
                return true
            }
        })
    }

    private fun showResult(container: Container<Show>?) {
        when {
            container == null -> {
                binding.resultsContainer.visibility = View.INVISIBLE
                binding.noresultsContainer.root.visibility = View.INVISIBLE
            }
            container.results.isEmpty() -> {
                binding.resultsContainer.visibility = View.INVISIBLE
                binding.noresultsContainer.root.visibility = View.VISIBLE
            }
            else -> {
                binding.resultsContainer.visibility = View.VISIBLE
                binding.noresultsContainer.root.visibility = View.INVISIBLE
                if (container.page == 1) {
                    adapter.clearEntities()
                }
                adapter.setEntities(container.results)
            }
        }
    }

    private fun showErrorView(show: Boolean) {
        if (show) {
            binding.errorContainer.root.visibility = View.VISIBLE
        } else {
            binding.errorContainer.root.visibility = View.INVISIBLE
        }
    }

    private fun showLoadingView(show: Boolean) {
        if (show) {
            binding.loadingContainer.root.visibility = View.VISIBLE
        } else {
            binding.loadingContainer.root.visibility = View.INVISIBLE
        }
    }
}
