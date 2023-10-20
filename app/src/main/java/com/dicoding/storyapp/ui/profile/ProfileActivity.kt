package com.dicoding.storyapp.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityProfileBinding
import com.dicoding.storyapp.ui.welcome.WelcomeActivity
import com.dicoding.storyapp.utils.ViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.profile)
        }

        profileViewModel.getUser().observe(this) {
            if (it.isLogin) {
                binding.tvAboutName.text = it.name
            } else {
                moveToWelcomeActivity()
            }
        }
    }

    private fun moveToWelcomeActivity() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}