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
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = ViewModelFactory.getInstance()

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

    private fun postText(){
        binding.apply {
            registerViewModel.postRegister(
                edRegisterName.text.toString(),
                edRegisterEmail.text.toString(),
                edRegisterPassword.text.toString()
            )
        }
    }

    private fun setupAction(){
        binding.apply {
            registerButton.setOnClickListener {
                when {
                    edRegisterName.length() == 0 -> {
                        edRegisterName.error = getString(R.string.required_field)
                    }
                    edRegisterEmail.length() == 0 -> {
                        edRegisterEmail.error = getString(R.string.required_field)
                    }
                    edRegisterPassword.length() == 0 -> {
                        edRegisterPassword.error = getString(R.string.required_field)
                    }
                    else -> {
                        edRegisterName.error = null
                        edRegisterEmail.error = null
                        edRegisterPassword.error = null

                        showLoading()
                        postText()
                        showToast()
                        moveActivity()
                    }
                }
            }
        }

        val tvToLogin = findViewById<TextView>(R.id.tv_toLogin)

        tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveActivity(){
        registerViewModel.registerResponse.observe(this){ response ->
            if (!response.error){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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

    private fun showLoading(){
        registerViewModel.isLoading.observe(this){
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showToast(){
        registerViewModel.toastText.observe(this){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText,Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}