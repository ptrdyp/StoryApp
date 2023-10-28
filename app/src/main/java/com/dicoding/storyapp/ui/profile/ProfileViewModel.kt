package com.dicoding.storyapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.di.UserModel
import com.dicoding.storyapp.data.di.UserPreference

class ProfileViewModel(private val preference: UserPreference): ViewModel() {
    fun getUser(): LiveData<UserModel>{
        return preference.getUser().asLiveData()
    }
}