package com.dicoding.storyapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val tvToLogin = findViewById<TextView>(R.id.tv_toLogin)

        tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}