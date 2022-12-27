package com.github.fernandospr.movies.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.databinding.ActivityDetailBinding
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.VideoAsset
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

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
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item: Show = intent.getParcelableExtra(EXTRA_ITEM)!!
        showDetail(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        ActivityCompat.finishAfterTransition(this)
        return true
    }

    private fun showDetail(item: Show) {
        if (item.overview.isNullOrBlank()) {
            binding.overviewTitle.visibility = View.GONE
            binding.overviewText.visibility = View.GONE
        } else {
            binding.overviewTitle.visibility = View.VISIBLE
            binding.overviewText.visibility = View.VISIBLE
        }
        binding.overviewText.text = item.overview
        binding.titleText.text = item.title
        binding.yearText.text = item.releaseDate

        setupImages(item)
        setupVideos(item)
    }

    private fun setupVideos(item: Show) {
        binding.videosContainer.title.text = getString(R.string.detail_videos)
        val adapter = VideosAdapter()
        adapter.setListener(object : VideosAdapter.Listener {
            override fun onItemClick(item: VideoAsset) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.youtubeVideoPath))
                startActivity(intent)
            }
        })
        binding.videosContainer.resultsContainer.adapter = adapter
        detailViewModel.getVideosLoading().observe(this, Observer { isLoading ->
            isLoading?.let { showLoadingVideosView(it) }
        })
        detailViewModel.getVideosError().observe(this, Observer { error ->
            error?.let { showErrorVideosView(it) }
        })
        detailViewModel.getVideos(item).observe(this, Observer { entities ->
            showVideos(adapter, entities)
        })
        binding.videosContainer.retryButton.setOnClickListener {
            detailViewModel.getVideos(item)
        }
    }

    private fun setupImages(item: Show) {
        Glide.with(this).load(item.posterFullPath)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_local_movies_24dp)
            .into(binding.previewImageView)

        if (!item.backdropFullPath.isNullOrBlank()) {
            Glide.with(this).asBitmap().load(item.backdropFullPath)
                .listener(
                    object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ) = false

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            if (resource != null) {
                                val palette = Palette.from(resource).generate()
                                binding.backdropOverlay.setBackgroundColor(
                                    palette.getMutedColor(
                                        getColor(
                                            R.color.colorItemBackground
                                        )
                                    )
                                )
                            }
                            return false
                        }

                    }
                )
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(binding.backdropImageView)
        }
    }

    private fun showVideos(adapter: VideosAdapter, container: Container<VideoAsset>?) {
        when {
            container == null -> {
                binding.videosContainer.resultsContainer.visibility = View.INVISIBLE
                binding.videosContainer.noresultsContainer.visibility = View.INVISIBLE
            }
            container.results.isEmpty() -> {
                binding.videosContainer.resultsContainer.visibility = View.INVISIBLE
                binding.videosContainer.noresultsContainer.visibility = View.VISIBLE
            }
            else -> {
                binding.videosContainer.resultsContainer.visibility = View.VISIBLE
                binding.videosContainer.noresultsContainer.visibility = View.INVISIBLE
                adapter.setEntities(container.results)
            }
        }
    }

    private fun showErrorVideosView(show: Boolean) {
        if (show) {
            binding.videosContainer.errorContainer.visibility = View.VISIBLE
        } else {
            binding.videosContainer.errorContainer.visibility = View.INVISIBLE
        }
    }

    private fun showLoadingVideosView(show: Boolean) {
        if (show) {
            binding.videosContainer.loadingContainer.visibility = View.VISIBLE
        } else {
            binding.videosContainer.loadingContainer.visibility = View.INVISIBLE
        }
    }
}
