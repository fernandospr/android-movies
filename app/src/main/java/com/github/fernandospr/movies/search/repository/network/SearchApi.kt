package com.github.fernandospr.movies.search.repository.network

import com.github.fernandospr.movies.common.repository.models.Container
import com.github.fernandospr.movies.common.repository.models.Show
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    // https://developers.themoviedb.org/3/search/multi-search
    @GET("search/multi")
    fun search(
        @Query("query") query: String,
        @Query("page") page: Int
    ): Single<Container<Show>>
}
