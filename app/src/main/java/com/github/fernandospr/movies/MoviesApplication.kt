package com.github.fernandospr.movies

import android.app.Application
import com.github.fernandospr.movies.common.di.dbModule
import com.github.fernandospr.movies.common.di.networkModule
import com.github.fernandospr.movies.detail.di.detailModule
import com.github.fernandospr.movies.detail.di.detailNetworkModule
import com.github.fernandospr.movies.main.di.mainModule
import com.github.fernandospr.movies.main.di.mainNetworkModule
import com.github.fernandospr.movies.search.di.searchModule
import com.github.fernandospr.movies.search.di.searchNetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(BuildConfig.KOIN_LOGGING)
            androidContext(this@MoviesApplication)
            modules(
                networkModule,
                dbModule,

                mainModule,
                mainNetworkModule,

                detailModule,
                detailNetworkModule,

                searchModule,
                searchNetworkModule
            )
        }
    }
}