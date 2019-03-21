package com.github.fernandospr.movies.repository.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtils(private val context: Context) {

    @Suppress("DEPRECATION")
    fun isConnectedToInternet(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < 23) {
            val activeNetwork = connectivityManager.activeNetworkInfo

            if (activeNetwork != null && activeNetwork.isConnected) {
                return activeNetwork.type == ConnectivityManager.TYPE_WIFI
                        || activeNetwork.type == ConnectivityManager.TYPE_MOBILE
            }
        } else {
            val activeNetwork = connectivityManager.activeNetwork

            if (activeNetwork != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }

        return false
    }
}