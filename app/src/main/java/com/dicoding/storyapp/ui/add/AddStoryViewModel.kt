package com.dicoding.storyapp.ui.add

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.di.UserModel
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.AddStoryResponse
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class AddStoryViewModel(private val preference: UserPreference) : ViewModel() {
    var currentImageUri: Uri? = null
    var description: String = ""
    private var currentLocation: Location? = null

    private val _addStory = MutableLiveData<AddStoryResponse>()
    val addStory: LiveData<AddStoryResponse> = _addStory

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

    suspend fun postStory(apiService: ApiService, file: MultipartBody.Part, description: RequestBody, currentLocation: Location? = null) {
        _isLoading.value = true
        try {
            val apiServiceWithToken = getApiServiceWithToken()
            if (apiServiceWithToken != null) {
                val response = apiService.uploadStory(file, description, currentLocation?.latitude, currentLocation?.longitude)
                if (response.isSuccessful) {
                    _addStory.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    val jsonInString = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _toastText.value = Event(errorBody.message ?: response.message().toString())
                }
            }
        } catch (e: HttpException){
            _toastText.value = Event(e.message ?: "An error occured")
            Log.e(TAG, "getStories: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    fun saveInstanceState(imageUri: Uri?, description: String, currentLocation: Location?) {
        currentImageUri = imageUri
        this.description = description
        this.currentLocation = currentLocation
    }

    companion object {
        const val TAG = "AddStoryViewModel"
    }
}