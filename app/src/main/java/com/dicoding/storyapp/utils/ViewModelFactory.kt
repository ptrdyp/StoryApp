package com.dicoding.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.di.Injection
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.di.dataStore
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.ui.add.AddStoryViewModel
import com.dicoding.storyapp.ui.detail.DetailViewModel
import com.dicoding.storyapp.ui.login.LoginViewModel
import com.dicoding.storyapp.ui.maps.MapsViewModel
import com.dicoding.storyapp.ui.profile.ProfileViewModel
import com.dicoding.storyapp.ui.register.RegisterViewModel
import com.dicoding.storyapp.ui.story.MainViewModel

class ViewModelFactory(private val repository: StoryRepository, private val preference: UserPreference, private val apiService: ApiService) :ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference, apiService) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference, repository, apiService) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(preference) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(preference) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(preference) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            val repository = Injection.provideRepository(context)
            val dataStore: DataStore<Preferences> = context.dataStore
            val preference = UserPreference.getInstance(dataStore)
            val apiService = ApiConfig.getApiService(token = "")
            instance ?: ViewModelFactory(repository, preference, apiService)
        }.also {
            instance = it
        }
    }
}