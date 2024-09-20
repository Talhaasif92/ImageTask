package com.devfast.imagetask.networking

import com.devfast.imagetask.model.ImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET(EP_SEARCH_RESULT)
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Response<ImageResponse>

}