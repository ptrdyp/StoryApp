package com.dicoding.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository): ViewModel() {
    val storiesWithLocation: LiveData<StoryResponse> = repository.storiesWithLocation
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }

    fun getApiServiceWithToken(): LiveData<ApiService?>{
        return liveData {
            emit(repository.getApiServiceWithToken())
        }
    }

    fun getStoriesWithLocation(apiService: ApiService) {
        viewModelScope.launch {
            repository.getStoriesWithLocation(apiService)
        }
    }

}