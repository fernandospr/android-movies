package com.github.fernandospr.movies.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.VideoAsset
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.videos_container.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ITEM = "EXTRA_ITEM"

        fun newIntent(context: Context, item: Show) =
                Intent(context, DetailActivity::class.java).apply {
                    putExtra(EXTRA_ITEM, item)
                }
    }

    private val detailViewModel: DetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item = intent.getParcelableExtra(EXTRA_ITEM) as Show
        showDetail(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        ActivityCompat.finishAfterTransition(this)
        return true
    }

    private fun showDetail(item: Show) {
        if (item.overview.isNullOrBlank()) {
            overviewTitle.visibility = View.GONE
            overviewText.visibility = View.GONE
        } else {
            overviewTitle.visibility = View.VISIBLE
            overviewText.visibility = View.VISIBLE
        }
        overviewText.text = item.overview
        titleText.text = item.title
        yearText.text = item.releaseDate

        setupImages(item)
        setupVideos(item)
    }

    private fun setupVideos(item: Show) {
        videosContainer.title.text = getString(R.string.detail_videos)
        val adapter = VideosAdapter()
        adapter.setListener(object : VideosAdapter.Listener {
            override fun onItemClick(item: VideoAsset) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.youtubeVideoPath))
                startActivity(intent)
            }
        })
        videosContainer.resultsContainer.adapter = adapter
        detailViewModel.getVideosLoading().observe(this, Observer { isLoading ->
            isLoading?.let { showLoadingVideosView(it) }
        })
        detailViewModel.getVideosError().observe(this, Observer { error ->
            error?.let { showErrorVideosView(it) }
        })
        detailViewModel.getVideos(item).observe(this, Observer { entities ->
            showVideos(adapter, entities)
        })
        videosContainer.errorContainer.retryButton.setOnClickListener {
            detailViewModel.getVideos(item)
        }
    }

    private fun setupImages(item: Show) {
        Glide.with(this).load(item.posterFullPath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_local_movies_24dp)
                .into(previewImageView)

        if (!item.backdropFullPath.isNullOrBlank()) {
            Glide.with(this).load(item.backdropFullPath)
                    .listener(
                            GlidePalette.with(item.backdropFullPath)
                                    .use(BitmapPalette.Profile.MUTED)
                                    .intoBackground(backdropOverlay)
                                    .crossfade(true)
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(backdropImageView)
        }
    }

    private fun showVideos(adapter: VideosAdapter, container: Container<VideoAsset>?) {
        when {
            container == null -> {
                videosContainer.resultsContainer.visibility = View.INVISIBLE
                videosContainer.noresultsContainer.visibility = View.INVISIBLE
            }
            container.results.isEmpty() -> {
                videosContainer.resultsContainer.visibility = View.INVISIBLE
                videosContainer.noresultsContainer.visibility = View.VISIBLE
            }
            else -> {
                videosContainer.resultsContainer.visibility = View.VISIBLE
                videosContainer.noresultsContainer.visibility = View.INVISIBLE
                adapter.setEntities(container.results)
            }
        }
    }

    private fun showErrorVideosView(show: Boolean) {
        if (show) {
            videosContainer.errorContainer.visibility = View.VISIBLE
        } else {
            videosContainer.errorContainer.visibility = View.INVISIBLE
        }
    }

    private fun showLoadingVideosView(show: Boolean) {
        if (show) {
            videosContainer.loadingContainer.visibility = View.VISIBLE
        } else {
            videosContainer.loadingContainer.visibility = View.INVISIBLE
        }
    }
}
