package com.dicoding.storyapp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.response.DetailStoryResponse
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel (private val preference: UserPreference) : ViewModel() {
    private val _storyItem = MutableLiveData<DetailStoryResponse>()
    val storyItem: MutableLiveData<DetailStoryResponse> = _storyItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    suspend fun getApiServiceWithToken(): ApiService? {
        val user = preference.getUser().first()
        return if (user.isLogin && user.token.isNotEmpty()) {
            ApiConfig.getApiService(user.token)
        } else {
            null
        }
    }

    suspend fun getDetailStory(apiService: ApiService, id: String = "") {
        _isLoading.value = true
        try {
            val apiServiceWithToken = getApiServiceWithToken()
            if (apiServiceWithToken != null) {
                val response = apiService.getDetailStory(id)
                if (response.isSuccessful) {
                    _storyItem.value = response.body()
                } else {
                    val jsonInString = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _toastText.value = Event(errorBody.message ?: response.message().toString())
                }
            }
        } catch (e: Exception) {
            _toastText.value = Event(e.message ?: "An error occured")
            Log.e(TAG, "getDetailStory: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    companion object {
        const val TAG = "DetailViewModel"
    }

}