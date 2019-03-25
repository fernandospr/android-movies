package com.github.fernandospr.movies.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.*

class DetailViewModel(private val repo: Repository) : ViewModel() {
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val error: MutableLiveData<Boolean> = MutableLiveData()
    private val videos: MutableLiveData<Container<VideoAsset>> = MutableLiveData()

    init {
        loading.value = false
        error.value = false
        videos.value = null
    }

    fun getVideosLoading(): LiveData<Boolean> = this.loading
    fun getVideosError(): LiveData<Boolean> = this.error

    fun getVideos(item: Show): LiveData<Container<VideoAsset>> {
        if (videos.value == null && loading.value == false) {
            loadVideos(item)
        }
        return videos
    }

    private fun loadVideos(item: Show) {
        loading.value = true
        error.value = false
        videos.value = null
        repo.loadVideos(item, 1, object : RepositoryCallback<Container<VideoAsset>> {
            override fun onSuccess(t: Container<VideoAsset>) {
                loading.value = false
                videos.value = t
            }

            override fun onError() {
                loading.value = false
                error.value = true
            }
        })
    }
}