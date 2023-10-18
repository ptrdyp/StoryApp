package com.dicoding.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    val list: LiveData<StoryResponse> = repository.list
    val listStoryItem: LiveData<List<ListStoryItem>> = repository.listStoryItem
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
}