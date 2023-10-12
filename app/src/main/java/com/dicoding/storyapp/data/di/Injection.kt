package com.dicoding.storyapp.data.di

import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}