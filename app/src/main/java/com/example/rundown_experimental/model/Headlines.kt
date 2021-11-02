package com.example.rundown_experimental.model

data class Headlines (

    val status: String,
    val totalResults: Int,
    val articles: List<Article>

    )