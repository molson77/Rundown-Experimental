package com.example.rundown_experimental.repository

import com.example.rundown_experimental.api.RetrofitInstance
import com.example.rundown_experimental.model.*
import retrofit2.Response

class RepositorySports {

    suspend fun getTeam(id: Int): Response<Teams> {
        return RetrofitInstance.apiSports.getTeam(id)
    }

    suspend fun getSeasons(): Response<Seasons> {
        return RetrofitInstance.apiSports.getSeasons()
    }

    suspend fun getEvents(id: Int, round: Int, season: String): Response<Events> {
        return RetrofitInstance.apiSports.getEvents(id, round, season)
    }

}