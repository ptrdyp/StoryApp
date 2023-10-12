package com.dicoding.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction(){
        val tvToLogin = findViewById<TextView>(R.id.tv_toLogin)

        tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000)
        val nameTv = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(1000)
        val nameEt = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val emailEt = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val passTv = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val passEt = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val button = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(1000)
        val tvAsk = ObjectAnimator.ofFloat(binding.tvAsktoLogin, View.ALPHA, 1f).setDuration(1000)
        val toLogin = ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(1000)

        val nameTogether = AnimatorSet().apply {
            playTogether(nameTv, nameEt)
        }

        val emailTogether = AnimatorSet().apply {
            playTogether(emailTv, emailEt)
        }

        val passTogether = AnimatorSet().apply {
            playTogether(passTv, passEt)
        }

        val toLoginTogether = AnimatorSet().apply {
            playTogether(tvAsk, toLogin)
        }

        AnimatorSet().apply {
            playSequentially(title, nameTogether, emailTogether, passTogether, button, toLoginTogether)
            start()
        }
    }

}