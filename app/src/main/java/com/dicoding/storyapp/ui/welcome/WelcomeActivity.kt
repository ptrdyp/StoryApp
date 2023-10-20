package com.dicoding.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.storyapp.databinding.ActivityWelcomeBinding
import com.dicoding.storyapp.ui.login.LoginActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun playAnimation(){
        fun View.createTranslationXAnimator(
            fromX: Float,
            toX: Float,
            duration: Long
        ) : ObjectAnimator {
            return ObjectAnimator.ofFloat(this, View.TRANSLATION_X, fromX, toX).apply {
                this.duration = duration
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        }

        binding.ivHello.createTranslationXAnimator(-30f, 30f, 6000).start()
        binding.imageView.createTranslationXAnimator(30f, -30f, 6000).start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(1000)
        val desc2 = ObjectAnimator.ofFloat(binding.descTextView2, View.ALPHA, 1f).setDuration(1000)
        val button = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(title, desc, desc2, button)
            start()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}