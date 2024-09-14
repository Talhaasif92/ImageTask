package com.devfast.imagetask.networking

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ImageApiClient {
    private const val API_KEY = "pWI7bUPy8bxs6yWLLTqdzpnA2J6TnQcb8SI6kLKu54Y7TSRVHZtTVFgQ"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.pexels.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", API_KEY)
                        .build()
                    chain.proceed(request)
                }
                .build()
        )
        .build()

    val imageService: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}