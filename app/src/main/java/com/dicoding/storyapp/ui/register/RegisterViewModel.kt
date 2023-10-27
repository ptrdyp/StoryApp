package com.dicoding.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse> = repository.registerResponse
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText

    fun postRegister(name: String, email: String, password: String){
        viewModelScope.launch {
            repository.postRegister(name, email, password)
        }
    }
}