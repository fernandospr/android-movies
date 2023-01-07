package com.github.fernandospr.movies.detail

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.fernandospr.movies.R
import com.google.android.material.appbar.AppBarLayout


class ScaleBehavior<V : View>(context: Context, attrs: AttributeSet)
    : CoordinatorLayout.Behavior<V>(context, attrs) {

    companion object {
        private const val MIN_SCALE = 0.15f
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        val appBarLayout = dependency as AppBarLayout

        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(appBarLayout.y) / maxScroll.toFloat()
        val scale = maxOf(MIN_SCALE, 1f - percentage)

        child.scaleX = scale
        child.scaleY = scale

        child.pivotY = appBarLayout.context.resources.getDimensionPixelSize(R.dimen.detailPivotY).toFloat()

        return true
    }
}