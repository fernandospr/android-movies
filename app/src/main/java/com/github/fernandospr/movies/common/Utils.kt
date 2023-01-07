package com.github.fernandospr.movies.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.setVisible(show: Boolean = true) {
    visibility = if (show) View.VISIBLE else View.INVISIBLE
}

fun View.setVisibleOrGone(show: Boolean = true) {
    visibility = if (show) View.VISIBLE else View.GONE
}