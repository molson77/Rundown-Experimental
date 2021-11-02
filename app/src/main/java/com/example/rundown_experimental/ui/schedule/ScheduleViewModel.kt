package com.example.rundown_experimental.ui.schedule

import com.example.rundown_experimental.model.Teams
import com.example.rundown_experimental.model.Events
import com.example.rundown_experimental.model.Seasons
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rundown_experimental.repository.RepositorySports
import kotlinx.coroutines.launch
import retrofit2.Response

private val repository: RepositorySports = RepositorySports()

class ScheduleViewModel() : ViewModel() {

    var teamResponse: MutableLiveData<Response<Teams>> = MutableLiveData()
    var seasonsResponse: MutableLiveData<Response<Seasons>> = MutableLiveData()
    var eventsResponse: MutableLiveData<Response<Events>> = MutableLiveData()

    fun getTeam(id: Int) {
        viewModelScope.launch {
            val response = repository.getTeam(id)
            teamResponse.value = response
        }
    }

    fun getSeasons() {
        viewModelScope.launch {
            val response = repository.getSeasons()
            seasonsResponse.value = response
        }
    }

    fun getEvents(id: Int, round: Int, season: String) {
        viewModelScope.launch {
            val response = repository.getEvents(id, round, season)
            eventsResponse.value = response
        }
    }
}