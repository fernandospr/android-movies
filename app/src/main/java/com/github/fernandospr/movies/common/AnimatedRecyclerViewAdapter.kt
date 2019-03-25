package com.github.fernandospr.movies.common

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView

abstract class AnimatedRecyclerViewAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private var lastPosition = 0

    private fun setAnimation(view: View) {
        val animation = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

        animation.duration = 200
        view.startAnimation(animation)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position > lastPosition) {
            setAnimation(holder.itemView)
            lastPosition = position
        }

    }

    override fun onViewDetachedFromWindow(holder: VH) {
        holder.itemView.clearAnimation()
    }
}