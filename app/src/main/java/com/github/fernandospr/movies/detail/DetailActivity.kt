package com.github.fernandospr.movies.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.models.VideoAsset
import com.github.fernandospr.movies.common.setVisible
import com.github.fernandospr.movies.common.setVisibleOrGone
import com.github.fernandospr.movies.databinding.ActivityDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity(), VideosAdapter.Listener {

    private lateinit var binding: ActivityDetailBinding
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

    private fun showDetail(item: Show) = with(binding) {
        overviewTitle.setVisibleOrGone(!item.overview.isNullOrBlank())
        overviewText.setVisibleOrGone(!item.overview.isNullOrBlank())
        overviewText.text = item.overview
        titleText.text = item.title
        yearText.text = item.releaseDate

        setupImages(item)
        setupVideos(item)
    }

    private fun setupVideos(item: Show) {
        binding.videosContainer.title.text = getString(R.string.detail_videos)

        val videosAdapter = VideosAdapter().apply {
            setListener(this@DetailActivity)
        }
        binding.videosContainer.resultsContainer.adapter = videosAdapter
        setupErrorContainer(item)
        bindViewModel(item, videosAdapter)
    }

    private fun bindViewModel(
        item: Show,
        videosAdapter: VideosAdapter
    ) {
        detailViewModel.getVideosLoading().observe(this) {
            binding.videosContainer.loadingContainer.setVisible(it)
        }
        detailViewModel.getVideosError().observe(this) {
            binding.videosContainer.errorContainer.setVisible(it)
        }
        detailViewModel.getVideos(item).observe(this) {
            showVideos(videosAdapter, it)
        }
    }

    private fun setupErrorContainer(item: Show) {
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

    private fun showVideos(
        videosAdapter: VideosAdapter,
        videos: Container<VideoAsset>?
    ) = with(binding.videosContainer) {
        when {
            videos == null -> {
                resultsContainer.setVisible(false)
                noresultsContainer.setVisible(false)
            }
            videos.results.isEmpty() -> {
                resultsContainer.setVisible(false)
                noresultsContainer.setVisible(true)
            }
            else -> {
                resultsContainer.setVisible(true)
                noresultsContainer.setVisible(false)
                videosAdapter.setEntities(videos.results)
            }
        }
    }

    // region VideosAdapter.Listener methods
    override fun onItemClick(item: VideoAsset) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.youtubeVideoPath))
        startActivity(intent)
    }
    // endregion

    companion object {
        private const val EXTRA_ITEM = "EXTRA_ITEM"

        fun newIntent(context: Context, item: Show) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_ITEM, item)
            }
    }
}
