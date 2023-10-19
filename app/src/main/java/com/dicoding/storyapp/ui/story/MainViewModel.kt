package com.dicoding.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    val listStoryItem: LiveData<List<ListStoryItem>> = repository.listStory
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getApiServiceWithToken(): LiveData<ApiService?>{
        return liveData {
            emit(repository.getApiServiceWithToken())
        }
    }

    fun getStories(apiService: ApiService){
        viewModelScope.launch {
            repository.getStories(apiService)
        }
    }
}