package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiService

class StoryRepository (private val storyDatabase: StoryDatabase, private val preference: UserPreference, private val apiService: ApiService){

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(preference, apiService)
            }
        ).liveData
    }


    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyDatabase: StoryDatabase, preference: UserPreference, apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, preference, apiService)
            }.also {
                instance = it
            }

        fun resetInstance() {
            instance = null
        }

    }
}