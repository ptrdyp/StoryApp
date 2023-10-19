package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.response.DetailStoryResponse
import com.dicoding.storyapp.utils.Event

class DetailViewModel (private val repository: StoryRepository) : ViewModel() {
    val storyItem: MutableLiveData<DetailStoryResponse> = repository.storyItem
    val toastText: LiveData<Event<String>> = repository.toastText

    fun getStoryById(id: String) {
        repository.getDetailStory(id)
    }
}