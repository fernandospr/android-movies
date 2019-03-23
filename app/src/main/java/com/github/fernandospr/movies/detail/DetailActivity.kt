package com.github.fernandospr.movies.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.repository.network.ApiItem
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.category_item.view.*

class DetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ITEM = "EXTRA_ITEM"

        fun newIntent(context: Context, item: ApiItem) =
                Intent(context, DetailActivity::class.java).apply {
                    putExtra(EXTRA_ITEM, item)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val item = intent.getParcelableExtra(EXTRA_ITEM) as ApiItem

        val posterImagePath = item.getPosterFullPath()
        val backdropImagePath = item.getBackdropFullPath()
        if (!posterImagePath.isNullOrBlank()) {
            Glide.with(this)
                    .load(posterImagePath)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
        }
        if (!backdropImagePath.isNullOrBlank()) {
            Glide.with(this)
                    .load(backdropImagePath)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(
                            GlidePalette.with(backdropImagePath)
                                    .use(BitmapPalette.Profile.MUTED)
                                    .intoBackground(backdropOverlay)
                                    .crossfade(true)
                    )
                    .into(backdropImageView)
        }
        overviewDescription.text = item.overview
        yearText.text = item.releaseDate
        videosContainer.category.text = getString(R.string.detail_videos)
    }
}
