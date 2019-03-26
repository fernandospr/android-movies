package com.github.fernandospr.movies.detail

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.common.inflate
import com.github.fernandospr.movies.repository.models.VideoAsset
import kotlinx.android.synthetic.main.video_item.view.*

class VideosAdapter : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    private var entities = listOf<VideoAsset>()
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setEntities(entities: List<VideoAsset>) {
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
        fun bind(item: VideoAsset) {
            title.text = item.name
            val imagePath = item.youtubeThumbnailPath
            if (!imagePath.isNullOrBlank()) {
                Glide.with(image.context)
                    .load(imagePath)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_local_movies_24dp)
            }
            itemView.setOnClickListener {
                listener?.onItemClick(item)
            }
        }
    }

    interface Listener {
        fun onItemClick(item: VideoAsset)
    }
}