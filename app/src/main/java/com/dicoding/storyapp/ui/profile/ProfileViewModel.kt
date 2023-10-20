package com.dicoding.storyapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserModel

class ProfileViewModel(private val repository: StoryRepository): ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}