package com.dicoding.storyapp.data.di

import android.content.Context
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val token = user.token
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService, pref)
    }
}