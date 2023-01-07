package com.github.fernandospr.movies.main

import android.view.ViewGroup
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.ItemAdapter
import com.github.fernandospr.movies.common.inflate

class CategoryAdapter : ItemAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item))
}