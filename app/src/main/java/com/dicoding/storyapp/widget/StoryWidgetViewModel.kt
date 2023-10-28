package com.dicoding.storyapp.widget

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.StoryWidgetRepository
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryWidgetViewModel(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val repository: StoryWidgetRepository
) : ViewModel() {

    fun refreshWidgetItems(imageLoader: ImageLoader, appWidgetId: Int, callback: (List<Bitmap>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    repository.refreshWidgetItems(imageLoader, appWidgetId) {
                        callback(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}