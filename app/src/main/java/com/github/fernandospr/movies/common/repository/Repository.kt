package com.github.fernandospr.movies.common.repository

import com.github.fernandospr.movies.common.repository.network.NetworkUtils
import io.reactivex.Single

abstract class Repository(private val networkUtils: NetworkUtils) {
    fun <T> fetch(
        withInternet: () -> Single<T>,
        withoutInternet: () -> Single<T>
    ) = if (networkUtils.isConnectedToInternet()) {
        withInternet()
    } else {
        withoutInternet()
    }
}