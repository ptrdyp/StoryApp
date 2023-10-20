package com.dicoding.storyapp.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.data.response.AddStoryResponse
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    val addStoryResponse: LiveData<AddStoryResponse> = repository.addStory
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText

    fun postStory(file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            repository.postStory(file, description)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}