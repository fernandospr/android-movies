package com.github.fernandospr.movies.detail

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.inflate
import com.github.fernandospr.movies.repository.network.ApiVideoResult
import kotlinx.android.synthetic.main.video_item.view.*

class VideosAdapter : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    private var entities = listOf<ApiVideoResult>()
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setEntities(entities: List<ApiVideoResult>) {
        this.entities = entities
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = entities.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.video_item))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.title
        val image = itemView.previewImageView
        fun bind(item: ApiVideoResult) {
            title.text = item.name
            val imagePath = item.youtubeThumbnailPath
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
        fun onItemClick(item: ApiVideoResult)
    }
}