package com.dicoding.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(position, params.loadSize)

            Log.d("StoryPagingSource", "Data loaded: $responseData")

            LoadResult.Page (
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            Log.e("StoryPagingSource", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}