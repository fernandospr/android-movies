package com.github.fernandospr.movies.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.fernandospr.movies.R
import com.github.fernandospr.movies.databinding.VideoItemBinding
import com.github.fernandospr.movies.repository.models.VideoAsset

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }


    inner class ViewHolder(binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val image = binding.previewImageView
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