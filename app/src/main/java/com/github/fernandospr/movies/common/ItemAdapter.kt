package com.github.fernandospr.movies.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.repository.Show

abstract class ItemAdapter : AnimatedRecyclerViewAdapter<RecyclerView.ViewHolder>() {

    private var entities = listOf<Show>()
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setEntities(entities: List<Show>) {
        val oldCount = itemCount
        this.entities = entities
        this.notifyItemInserted(oldCount)
    }

    fun clearEntities() {
        this.entities = emptyList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = entities.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as ViewHolder).bind(entities[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val image = itemView.findViewById<ImageView>(R.id.imageView)
        fun bind(item: Show) {
            title.text = item.title
            val imagePath = item.getPosterFullPath()
            if (!imagePath.isNullOrBlank()) {
                Glide.with(image.context)
                    .load(imagePath)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_local_movies_24dp)
            }
            itemView.setOnClickListener {
                listener?.onItemClick(image, item)
            }
        }
    }

    interface Listener {
        fun onItemClick(view: View, item: Show)
    }
}