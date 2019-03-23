package com.github.fernandospr.movies.main

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.inflate
import com.github.fernandospr.movies.repository.network.ApiItem
import kotlinx.android.synthetic.main.search_item.view.*

// FIXME: Similar to SearchAdapter?
class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var entities = listOf<ApiItem>()
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setEntities(entities: List<ApiItem>) {
        this.entities = entities
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = entities.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.title
        val image = itemView.imageView
        fun bind(item: ApiItem) {
            title.text = item.title
            val imagePath = item.getPosterFullPath()
            if (!imagePath.isNullOrBlank()) {
                Glide.with(image.context)
                    .load(imagePath)
                    .into(image)
            }
            itemView.setOnClickListener {
                listener?.onItemClick(item)
            }
        }
    }

    interface Listener {
        fun onItemClick(item: ApiItem)
    }
}