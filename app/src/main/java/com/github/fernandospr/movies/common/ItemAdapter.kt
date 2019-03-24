package com.github.fernandospr.movies.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.repository.network.ApiItem

abstract class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var entities = listOf<ApiItem>()
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setEntities(entities: List<ApiItem>) {
        val oldCount = itemCount
        this.entities = entities
        this.notifyItemInserted(oldCount)
    }

    fun clearEntities() {
        this.entities = emptyList()
    }

    override fun getItemCount(): Int = entities.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val image = itemView.findViewById<ImageView>(R.id.imageView)
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