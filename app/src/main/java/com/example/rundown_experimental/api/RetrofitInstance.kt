package com.example.rundown_experimental.api

import retrofit2.Retrofit
import com.example.rundown_experimental.util.Constants.Companion.BASE_URL_SPORTS
import com.example.rundown_experimental.util.Constants.Companion.BASE_URL_NEWS
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofitSports by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SPORTS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitNews by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_NEWS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiSports: SportsAPI by lazy {
        retrofitSports.create(SportsAPI::class.java)
    }

    val apiNews: NewsAPI by lazy {
        retrofitNews.create(NewsAPI::class.java)
    }
}