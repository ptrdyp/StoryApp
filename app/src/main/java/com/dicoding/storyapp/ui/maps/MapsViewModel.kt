package com.dicoding.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.di.UserModel
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class MapsViewModel(private val preference: UserPreference): ViewModel() {
    private val _storiesWithLocation = MutableLiveData<StoryResponse>()
    val storiesWithLocation: MutableLiveData<StoryResponse> = _storiesWithLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun getUser(): LiveData<UserModel>{
        return preference.getUser().asLiveData()
    }

    suspend fun getApiServiceWithToken(): ApiService? {
        val user = preference.getUser().first()
        return if (user.isLogin && user.token.isNotEmpty()) {
            ApiConfig.getApiService(user.token)
        } else {
            null
        }
    }

    suspend fun getStoriesWithLocation(apiService: ApiService) {
        _isLoading.value = true
        try {
            val apiServiceWithToken = getApiServiceWithToken()
            if (apiServiceWithToken != null) {
                val response = apiService.getStoriesWithLocation(1)
                if (response.isSuccessful) {
                    _storiesWithLocation.value = response.body()
                } else {
                    val jsonInString = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _toastText.value = Event(errorBody.message ?: response.message().toString())
                }
            }
        } catch (e: Exception) {
            _toastText.value = Event(e.message ?: "An error occured")
            Log.e(TAG, "getStoriesWithLocation: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    companion object {
        const val TAG = "MapsViewModel"
    }
}