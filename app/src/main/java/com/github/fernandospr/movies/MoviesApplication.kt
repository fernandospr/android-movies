package com.github.fernandospr.movies

import android.app.Application
import com.github.fernandospr.movies.di.appModule
import com.github.fernandospr.movies.di.dbModule
import com.github.fernandospr.movies.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@MoviesApplication)
            modules(appModule, networkModule, dbModule)
        }
    }
}