package com.github.fernandospr.movies.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.EndlessRecyclerViewScrollListener
import com.github.fernandospr.movies.common.ItemAdapter
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.setVisible
import com.github.fernandospr.movies.databinding.ActivityMainBinding
import com.github.fernandospr.movies.databinding.CategoryItemBinding
import com.github.fernandospr.movies.detail.DetailActivity
import com.github.fernandospr.movies.search.SearchActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), ItemAdapter.Listener {

    private lateinit var binding: ActivityMainBinding

    private val popularMoviesViewModel: PopularMoviesViewModel by viewModel()
    private val popularTvShowsViewModel: PopularTvShowsViewModel by viewModel()
    private val topRatedMoviesViewModel: TopRatedMoviesViewModel by viewModel()
    private val topRatedTvShowsViewModel: TopRatedTvShowsViewModel by viewModel()
    private val upcomingMoviesViewModel: UpcomingMoviesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            setupCategory(
                popularMoviesContainer,
                getString(R.string.category_popular) + " " + getString(R.string.category_movies),
                popularMoviesViewModel
            )

            setupCategory(
                popularTvShowsContainer,
                getString(R.string.category_popular) + " " + getString(R.string.category_tvshows),
                popularTvShowsViewModel
            )

            setupCategory(
                topRatedMoviesContainer,
                getString(R.string.category_toprated) + " " + getString(R.string.category_movies),
                topRatedMoviesViewModel
            )

            setupCategory(
                topRatedTvShowsContainer,
                getString(R.string.category_toprated) + " " + getString(R.string.category_tvshows),
                topRatedTvShowsViewModel
            )

            setupCategory(
                upcomingMoviesContainer,
                getString(R.string.category_upcoming) + " " + getString(R.string.category_movies),
                upcomingMoviesViewModel
            )

            swipeRefreshLayout.setOnRefreshListener {
                popularMoviesViewModel.getItems(forceRefresh = true)
                popularTvShowsViewModel.getItems(forceRefresh = true)
                topRatedMoviesViewModel.getItems(forceRefresh = true)
                topRatedTvShowsViewModel.getItems(forceRefresh = true)
                upcomingMoviesViewModel.getItems(forceRefresh = true)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setupCategory(
        categoryItemBinding: CategoryItemBinding,
        title: String,
        viewModel: MainViewModel
    ) {
        categoryItemBinding.category.text = title

        val categoryAdapter = CategoryAdapter().apply {
            setListener(this@MainActivity)
        }
        setupResultsContainer(categoryItemBinding, categoryAdapter, viewModel)
        setupErrorContainer(categoryItemBinding, viewModel)
        bindViewModel(viewModel, categoryItemBinding, categoryAdapter)
    }

    private fun setupErrorContainer(
        categoryItemBinding: CategoryItemBinding,
        viewModel: MainViewModel
    ) {
        categoryItemBinding.retryButton.setOnClickListener {
            viewModel.getItems()
        }
    }

    private fun setupResultsContainer(
        categoryItemBinding: CategoryItemBinding,
        categoryAdapter: CategoryAdapter,
        viewModel: MainViewModel
    ) = with(categoryItemBinding.resultsContainer) {
        adapter = categoryAdapter
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

    private fun bindViewModel(
        viewModel: MainViewModel,
        categoryItemBinding: CategoryItemBinding,
        categoryAdapter: CategoryAdapter
    ) {
        viewModel.getLoading().observe(this) {
            categoryItemBinding.loadingContainer.setVisible(it)
        }
        viewModel.getError().observe(this) {
            categoryItemBinding.errorContainer.setVisible(it)
        }
        viewModel.getItems().observe(this) {
            showItems(categoryItemBinding, categoryAdapter, it)
        }
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

    private fun showItems(
        categoryItemBinding: CategoryItemBinding,
        categoryAdapter: CategoryAdapter,
        items: Container<Show>?
    ) = with(categoryItemBinding) {
        when {
            items == null -> {
                resultsContainer.setVisible(false)
                noresultsContainer.setVisible(false)
            }
            items.results.isEmpty() -> {
                resultsContainer.setVisible(false)
                noresultsContainer.setVisible(true)
            }
            else -> {
                resultsContainer.setVisible(true)
                noresultsContainer.setVisible(false)
                if (items.page == 1) {
                    categoryAdapter.clearEntities()
                }
                categoryAdapter.setEntities(items.results)
            }
        }
    }

    // region ItemAdapter.Listener methods
    override fun onItemClick(view: View, item: Show) {
        val intent = DetailActivity.newIntent(this, item)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            view,
            ViewCompat.getTransitionName(view)!!
        )
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }
    // endregion
}
