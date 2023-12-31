package com.dicoding.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupData()
    }

    private fun setupData() {
        val id = intent.getStringExtra(EXTRA_ID) ?: ""

        lifecycleScope.launch {
            val apiService = detailViewModel.getApiServiceWithToken()

            if (apiService != null) {
                detailViewModel.getDetailStory(apiService, id)
            } else {
                showToast()
            }
        }

        detailViewModel.storyItem.observe(this) {
            Log.d("DetailActivity", "StoryItem observer called with value: $it")
            if (it != null) {
                binding.apply {
                    Glide.with(this@DetailActivity)
                        .load(it.story.photoUrl)
                        .fitCenter()
                        .into(ivDetailPhoto)
                    tvDetailName.text = it.story.name
                    tvDetailDescription.text = it.story.description
                }
            } else {
                showToast()
            }
        }
    }

    private fun showToast(){
        detailViewModel.toastText.observe(this){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}