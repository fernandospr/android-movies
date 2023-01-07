package com.github.fernandospr.movies.common.repository

import com.github.fernandospr.movies.common.repository.network.Network
import io.reactivex.Single

abstract class Repository(private val network: Network) {
    fun <T> fetch(
        online: () -> Single<T>,
        offline: () -> Single<T>
    ) = if (network.isOnline()) {
        online()
    } else {
        offline()
    }
}