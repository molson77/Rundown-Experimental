package com.example.rundown_experimental.api

import com.example.rundown_experimental.model.Headlines
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("everything")
    suspend fun getHeadlines(
        @Query("qInTitle") query: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String
    ): Response<Headlines>

}