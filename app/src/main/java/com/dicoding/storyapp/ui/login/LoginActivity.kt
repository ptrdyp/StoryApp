package com.dicoding.storyapp.ui.login

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
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.register.RegisterActivity
import com.dicoding.storyapp.ui.story.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = ViewModelFactory.getInstance(this)

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

    private fun saveUser(userModel: UserModel) {
        loginViewModel.saveUser(userModel)
    }

    private fun postText() {
        binding.apply {
            loginViewModel.postLogin(
                edLoginEmail.text.toString(),
                edLoginPassword.text.toString()
            )
        }

        loginViewModel.loginResponse.observe(this) {
            saveUser(
                UserModel(
                    it.loginResult?.name.toString(),
                    it.loginResult?.token.toString(),
                    true
                )
            )
        }
    }

    private fun setupAction() {
        binding.apply {
            loginButton.setOnClickListener {
                when {
                    edLoginEmail.length() == 0 -> {
                        edLoginEmail.error = getString(R.string.required_field)
                    }
                    edLoginPassword.length() == 0 -> {
                        edLoginPassword.error = getString(R.string.required_field)
                    }
                    else -> {
                        edLoginEmail.error = null
                        edLoginPassword.error = null

                        showLoading()
                        postText()
                        showToast()
                        loginViewModel.login()
                        moveActivity()
                    }
                }
            }
        }

        val tvToRegister = findViewById<TextView>(R.id.tv_toRegister)

        tvToRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveActivity(){
        loginViewModel.loginResponse.observe(this) {
            if (!it.error){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
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

        binding.ivCloud.createTranslationXAnimator(-30f, 60f, 6000).start()
        binding.imageView.createTranslationXAnimator(30f, -30f, 6000).start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(1000)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val emailEt = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val passTv = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val passEt = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val button = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(1000)
        val tvAsk = ObjectAnimator.ofFloat(binding.tvAsktoRegister, View.ALPHA, 1f).setDuration(1000)
        val toRegister = ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(1000)

        val emailTogether = AnimatorSet().apply {
            playTogether(emailTv, emailEt)
        }

        val passTogether = AnimatorSet().apply {
            playTogether(passTv, passEt)
        }

        val toRegisTogether = AnimatorSet().apply {
            playTogether(tvAsk, toRegister)
        }

        AnimatorSet().apply {
            playSequentially(title, message, emailTogether, passTogether, button, toRegisTogether)
            start()
        }

    }

    private fun showLoading() {
        loginViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showToast() {
        loginViewModel.toastText.observe(this){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}