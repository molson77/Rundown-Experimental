package com.example.rundown_experimental.repository

import com.example.rundown_experimental.api.RetrofitInstance
import com.example.rundown_experimental.model.Headlines
import com.example.rundown_experimental.util.Constants.Companion.NEWS_API_KEY
import retrofit2.Response

class RepositoryNews {

    suspend fun getHeadlines(query: String): Response<Headlines> {
        return RetrofitInstance.apiNews.getHeadlines(query, "publishedAt", NEWS_API_KEY)
    }
}