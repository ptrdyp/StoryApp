package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.DetailStoryResponse
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetailViewModel (private val repository: StoryRepository) : ViewModel() {
    val storyItem: MutableLiveData<DetailStoryResponse> = repository.storyItem
    val toastText: LiveData<Event<String>> = repository.toastText

    fun getApiServiceWithToken(): LiveData<ApiService?>{
        return liveData {
            emit(repository.getApiServiceWithToken())
        }
    }

    fun getStoryById(apiService: ApiService, id: String) {
        viewModelScope.launch {
            repository.getDetailStory(apiService, id)
        }
    }

}