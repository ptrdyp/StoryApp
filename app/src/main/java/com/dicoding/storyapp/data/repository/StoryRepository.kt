package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.StoryPagingSource
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import java.util.concurrent.Flow

class StoryRepository (private val storyDatabase: StoryDatabase, private val apiService: ApiService){

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also {
                instance = it
            }
    }
}