package com.github.fernandospr.movies.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.repository.network.ApiItem
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import kotlinx.android.synthetic.main.activity_detail.*


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

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item = intent.getParcelableExtra(EXTRA_ITEM) as ApiItem
        showDetail(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showDetail(item: ApiItem) {
        overviewText.text = item.overview
        titleText.text = item.title
        yearText.text = item.releaseDate

        val posterPath = item.getPosterFullPath()
        if (!posterPath.isNullOrBlank()) {
            Glide.with(this).load(posterPath)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
        }

        val backdropPath = item.getBackdropFullPath()
        if (!backdropPath.isNullOrBlank()) {
            Glide.with(this).load(backdropPath)
                    .listener(
                            GlidePalette.with(backdropPath)
                                    .use(BitmapPalette.Profile.MUTED)
                                    .intoBackground(backdropOverlay)
                                    .crossfade(true)
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(backdropImageView)
        }
    }
}
