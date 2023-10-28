package com.dicoding.storyapp.data.di

import android.content.Context
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(storyDatabase, apiService)
    }
}