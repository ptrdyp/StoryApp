package com.dicoding.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.di.UserModel
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val preference: UserPreference, private val repository: StoryRepository, private val apiService: ApiService) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse?>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel>{
        return preference.getUser().asLiveData()
    }

    suspend fun logout() {
        preference.logout()
        _loginResponse.value = null
    }

    suspend fun getApiServiceWithToken(): ApiService? {
        val user = preference.getUser().first()
        return if (user.isLogin && user.token.isNotEmpty()) {
            ApiConfig.getApiService(user.token)
        } else {
            null
        }
    }

//    fun getStories(apiService: ApiService) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                repository.getStories(apiService).cachedIn(viewModelScope)
//                _isLoading.value = false
//            } catch (e: Exception) {
//                _isLoading.value = false
//                _toastText.value = Event("Error loading stories: ${e.message}")
//            }
//        }
//    }

    companion object {
        const val TAG = "MainViewModel"
    }
}