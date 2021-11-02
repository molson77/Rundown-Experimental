package com.example.rundown_experimental.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rundown_experimental.model.Headlines
import com.example.rundown_experimental.model.Teams
import com.example.rundown_experimental.repository.RepositoryNews
import kotlinx.coroutines.launch
import retrofit2.Response

private val repository: RepositoryNews = RepositoryNews()

class MediaViewModel() : ViewModel() {

    var headlinesResponse: MutableLiveData<Response<Headlines>> = MutableLiveData()

    fun getHeadlines(query: String) {
        viewModelScope.launch {
            val response = repository.getHeadlines(query)
            headlinesResponse.value = response
        }
    }
}