package com.github.fernandospr.movies.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.EndlessRecyclerViewScrollListener
import com.github.fernandospr.movies.common.ItemAdapter
import com.github.fernandospr.movies.detail.DetailActivity
import com.github.fernandospr.movies.repository.network.ApiItem
import com.github.fernandospr.movies.repository.network.ApiItemsContainer
import com.github.fernandospr.movies.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.category_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val popularMoviesViewModel: PopularMoviesViewModel by viewModel()
    private val popularTvShowsViewModel: PopularTvShowsViewModel by viewModel()
    private val topRatedMoviesViewModel: TopRatedMoviesViewModel by viewModel()
    private val topRatedTvShowsViewModel: TopRatedTvShowsViewModel by viewModel()
    private val upcomingMoviesViewModel: UpcomingMoviesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCategory(popularMoviesContainer,
                getString(R.string.category_popular) + " " + getString(R.string.category_movies),
                popularMoviesViewModel)

        setupCategory(popularTvShowsContainer,
                getString(R.string.category_popular) + " " + getString(R.string.category_tvshows),
                popularTvShowsViewModel)

        setupCategory(topRatedMoviesContainer,
                getString(R.string.category_toprated) + " " + getString(R.string.category_movies),
                topRatedMoviesViewModel)

        setupCategory(topRatedTvShowsContainer,
                getString(R.string.category_toprated) + " " + getString(R.string.category_tvshows),
                topRatedTvShowsViewModel)

        setupCategory(upcomingMoviesContainer,
                getString(R.string.category_upcoming) + " " + getString(R.string.category_movies),
                upcomingMoviesViewModel)
    }

    private fun setupCategory(container: View, title: String, viewModel: MainViewModel) {
        container.category.text = title

        val adapter = CategoryAdapter()
        adapter.setListener(object : ItemAdapter.Listener {
            override fun onItemClick(item: ApiItem) {
                val intent = DetailActivity.newIntent(this@MainActivity, item)
                startActivity(intent)
            }
        })
        container.resultsContainer.adapter = adapter
        viewModel.getLoading().observe(this, Observer { isLoading ->
            isLoading?.let { showLoadingView(container, it) }
        })
        viewModel.getError().observe(this, Observer { error ->
            error?.let { showErrorView(container, it) }
        })
        viewModel.getItems().observe(this, Observer { entities ->
            showResult(container, adapter, entities)
        })
        container.errorContainer.retryButton.setOnClickListener {
            viewModel.getItems()
        }
        val scrollListener = object : EndlessRecyclerViewScrollListener(
                container.resultsContainer.layoutManager as LinearLayoutManager
        ) {
            override fun onLoadMore(page: Int,
                                    totalItemsCount: Int,
                                    view: RecyclerView?) {
                viewModel.getNextPageItems()
            }
        }
        container.resultsContainer.addOnScrollListener(scrollListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            val intent = SearchActivity.newIntent(this)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showResult(container: View,
                           adapter: CategoryAdapter,
                           itemsContainer: ApiItemsContainer?) {
        when {
            itemsContainer == null -> {
                container.resultsContainer.visibility = View.INVISIBLE
                container.noresultsContainer.visibility = View.INVISIBLE
            }
            itemsContainer.results.isEmpty() -> {
                container.resultsContainer.visibility = View.INVISIBLE
                container.noresultsContainer.visibility = View.VISIBLE
            }
            else -> {
                container.resultsContainer.visibility = View.VISIBLE
                container.noresultsContainer.visibility = View.INVISIBLE
                adapter.setEntities(itemsContainer.results)
            }
        }
    }

    private fun showErrorView(container: View, show: Boolean) {
        if (show) {
            container.errorContainer.visibility = View.VISIBLE
        } else {
            container.errorContainer.visibility = View.INVISIBLE
        }
    }

    private fun showLoadingView(container: View, show: Boolean) {
        if (show) {
            container.loadingContainer.visibility = View.VISIBLE
        } else {
            container.loadingContainer.visibility = View.INVISIBLE
        }
    }
}
