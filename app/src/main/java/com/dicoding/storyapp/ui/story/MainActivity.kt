package com.dicoding.storyapp.ui.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.ui.add.AddStoryActivity
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.ui.profile.ProfileActivity
import com.dicoding.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.app_name)
        }

        ViewModelFactory.getInstance(this)

        setupAdapter()
        moveToAddStoryActivity()
        setupUser()
    }

    private fun setupAdapter(){
        adapter = StoryAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStory.layoutManager = layoutManager
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }

        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: ListStoryItem, binding: ItemStoryBinding) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, data.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(binding.ivItemPhoto, "photo"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDescription, "description"),
                    )
                startActivity(intent, optionsCompat.toBundle())
            }
        })
    }

    private fun setupUser() {
        showLoading()
        mainViewModel.getUser().observe(this) {
            Log.d("MainActivity", "User isLogin: ${it.isLogin}")
            token = it.token
            if (it.isLogin) {
                Log.d("TokenDebug", "Stored Token: $token")
                Log.d("TokenDebug", "Sending request to get stories with token: $token")
                setupData()
            } else {
                moveToWelcomeActivity()
            }
        }
        showToast()
    }

    private fun setupData(){
        mainViewModel.listStoryItem.observe(this) {
            if (it != null && it.isNotEmpty()) {
                adapter.setList(it)
            } else {
                showToast()
            }
        }
        mainViewModel.getApiServiceWithToken().observe(this) {  apiService ->
            if (apiService != null) {
                mainViewModel.getStories(apiService)
            } else {
                showToast()
            }
        }
    }

    private fun moveToWelcomeActivity() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    private fun moveToAddStoryActivity() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun moveToProfileActivity() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.profileButton -> {
                moveToProfileActivity()
                true
            }
            R.id.logoutButton -> {
                mainViewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading() {
        mainViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showToast(){
        mainViewModel.toastText.observe(this){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}