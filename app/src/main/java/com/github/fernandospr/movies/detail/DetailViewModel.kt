package com.github.fernandospr.movies.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.fernandospr.movies.common.BaseViewModel
import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import com.github.fernandospr.movies.common.repository.models.VideoAsset
import com.github.fernandospr.movies.detail.repository.DetailRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val repo: DetailRepository) : BaseViewModel() {
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val error: MutableLiveData<Boolean> = MutableLiveData()
    private val videos: MutableLiveData<Container<VideoAsset>?> = MutableLiveData()

    init {
        loading.value = false
        error.value = false
        videos.value = null
    }

    fun getVideosLoading(): LiveData<Boolean> = this.loading
    fun getVideosError(): LiveData<Boolean> = this.error

    fun getVideos(item: Show): LiveData<Container<VideoAsset>?> {
        if (videos.value == null && loading.value == false) {
            loadVideos(item)
        }
        return videos
    }

    private fun loadVideos(item: Show) {
        loading.value = true
        error.value = false
        videos.value = null

        disposable.add(
            repo.loadVideos(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        loading.value = false
                        videos.value = it
                    },
                    {
                        loading.value = false
                        error.value = true
                    }
                )
        )
    }
}
