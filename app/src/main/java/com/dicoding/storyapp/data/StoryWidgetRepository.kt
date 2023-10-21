package com.dicoding.storyapp.data

import android.appwidget.AppWidgetManager
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import com.dicoding.storyapp.widget.ImageLoader
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryWidgetRepository private constructor(
    private val apiService: ApiService,
    private val pref: UserPreference,
    private val appWidgetId: Int,
    private val appWidgetManager: AppWidgetManager
) {
    private val widgetItems = ArrayList<ListStoryItem>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    suspend fun refreshWidgetItems(imageLoader: ImageLoader, appWidgetId: Int, callback: (List<Bitmap>) -> Unit) {
        withContext(Dispatchers.Main) {
            try {
                widgetItems.clear()

                val listStoryItem = getWidgetItems()
                val bitmapList = mutableListOf<Bitmap>()

                for (storyItem in listStoryItem) {
                    imageLoader.loadImage(storyItem.photoUrl) { bitmap ->
                        if (bitmap != null) {
                            widgetItems.add(storyItem)
                            bitmapList.add(bitmap)

                            if (widgetItems.size == listStoryItem.size) {
                                callback(bitmapList)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _toastText.value = Event(e.message ?: "An error occured")
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getWidgetItems(): List<ListStoryItem> {
        val response = apiService.getStories()
        if (response.isSuccessful) {
            response.body()?.listStory ?: emptyList()
        } else {
            val jsonInString = response.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            _toastText.value = Event(errorBody.message ?: response.message().toString())
            emptyList()
        }
        return response.body()?.listStory ?: emptyList()
    }

    companion object {

        fun create(
            apiService: ApiService,
            userPreference: UserPreference,
            appWidgetId: Int,
            appWidgetManager: AppWidgetManager
        ): StoryWidgetRepository {
            return StoryWidgetRepository(apiService, userPreference, appWidgetId, appWidgetManager)
        }
    }
}