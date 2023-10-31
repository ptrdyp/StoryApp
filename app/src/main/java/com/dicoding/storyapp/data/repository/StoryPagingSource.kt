package com.dicoding.storyapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val preference: UserPreference, private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = preference.getUser().first().token
            val responseData = apiService.getStories(position, params.loadSize)

            if (token.isNotEmpty()) {
                if (responseData.isSuccessful) {

                    LoadResult.Page (
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                } else {
                    LoadResult.Error(Exception("Failed"))
                }
            } else {
                LoadResult.Error(Exception("Failed"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}