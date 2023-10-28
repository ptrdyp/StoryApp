package com.dicoding.storyapp.ui.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.ui.add.AddStoryActivity
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.ui.maps.MapsActivity
import com.dicoding.storyapp.ui.profile.ProfileActivity
import com.dicoding.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

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
        setupUser()
        moveToAddStoryActivity()
    }

    private fun setupAdapter(){
        adapter = StoryAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStory.adapter = adapter
            rvStory.layoutManager = layoutManager
            rvStory.setHasFixedSize(true)
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
                setupData()
            } else {
                moveToWelcomeActivity()
            }
        }
        showToast()
    }

    private fun setupData(){
        lifecycleScope.launch{
            val apiService = mainViewModel.getApiServiceWithToken()
            if (apiService != null) {
                mainViewModel.story
            } else {
                showToast()
            }
        }

        mainViewModel.story.observe(this) {
            lifecycleScope.launch {
                try {
                    adapter.submitData(it)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error during data submission", e)
                }
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

    private fun setupSetting() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.mapsButton -> {
                moveToMapsActivity()
                true
            }
            R.id.profileButton -> {
                moveToProfileActivity()
                true
            }
            R.id.settingLanguage -> {
                setupSetting()
                true
            }
            R.id.logoutButton -> {
                lifecycleScope.launch {
                    mainViewModel.logout()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun moveToMapsActivity(){
        startActivity(Intent(this, MapsActivity::class.java))
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}