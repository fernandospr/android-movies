package com.github.fernandospr.movies.detail.di

import com.github.fernandospr.movies.detail.DetailViewModel
import com.github.fernandospr.movies.detail.repository.DetailRepository
import com.github.fernandospr.movies.detail.repository.DetailRepositoryImpl
import com.github.fernandospr.movies.detail.repository.network.DetailApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailModule = module {
    single<DetailRepository> {
        DetailRepositoryImpl(get<DetailApi>())
    }

    viewModel { DetailViewModel(get()) }
}