package com.github.fernandospr.movies.common.repository.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkUtilsImpl(private val context: Context) : NetworkUtils {

    override fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork

        if (activeNetwork != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }

        return false
    }
}