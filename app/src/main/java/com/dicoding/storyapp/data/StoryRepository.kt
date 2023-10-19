package com.dicoding.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.utils.Event
import com.google.gson.Gson
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val pref: UserPreference
){
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: MutableLiveData<LoginResponse?> = _loginResponse

    private val _listStory = MutableLiveData<StoryResponse>()
    val listStory: LiveData<StoryResponse> = _listStory

    private val _storyItem = MutableLiveData<List<ListStoryItem>>()
    val storyItem: LiveData<List<ListStoryItem>> = _storyItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)

        client.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                try {
                    if (response.body() != null){
                        _registerResponse.value = response.body()
                        _toastText.value = Event(response.body()?.message.toString())
                    } else {
                        val jsonInString = response.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        _toastText.value = Event(errorBody.message ?: response.message().toString())
                    }
                } catch (e: HttpException){
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _toastText.value = Event(errorBody.message ?: response.message().toString())

                    Log.e(TAG, "onFailure: ${response.message()}, ${response.body()?.message.toString()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.login(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                try {
                    if (response.body() != null){
                        _loginResponse.value = response.body()
                        _toastText.value = Event(response.body()?.message.toString())
                    } else {
                        val jsonInString = response.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        _toastText.value = Event(errorBody.message ?: response.message().toString())
                    }
                } catch (e: HttpException){
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _toastText.value = Event(errorBody.message ?: response.message().toString())

                    Log.e(TAG, "onFailure: ${response.message()}, ${response.body()?.message.toString()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    suspend fun getApiServiceWithToken(): ApiService? {
        val user = pref.getUser().first()
        return if (user.isLogin) {
            ApiConfig.getApiService(user.token)
        } else {
            null
        }
    }

    suspend fun getStories(apiService: ApiService){
        _isLoading.value = true
        try {
            val response = apiService.getStories()
            if (response.isSuccessful) {
                _listStory.value = response.body()
            } else {
                val jsonInString = response.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                _toastText.value = Event(errorBody.message ?: response.message().toString())
            }
        } catch (e: Exception) {
            _toastText.value = Event(e.message ?: "An error occured")
            Log.e(TAG, "getStories: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun saveUser(userModel: UserModel) {
        pref.saveUser(userModel)
    }

    fun getUser(): LiveData<UserModel>{
        return pref.getUser().asLiveData()
    }

    suspend fun logout() {
        pref.logout()
        _loginResponse.value = null
    }

    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference) =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref)
            }.also {
                instance = it
            }
    }
}