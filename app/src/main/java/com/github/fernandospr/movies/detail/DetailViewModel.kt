package com.github.fernandospr.movies.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.fernandospr.movies.repository.Repository
import com.github.fernandospr.movies.repository.models.Container
import com.github.fernandospr.movies.repository.models.Show
import com.github.fernandospr.movies.repository.models.VideoAsset
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val repo: Repository) : ViewModel() {
    private val disposable = CompositeDisposable()
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

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}