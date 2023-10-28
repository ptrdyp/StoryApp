package com.dicoding.storyapp.ui.add

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.ui.story.MainActivity
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.getImageUri
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class  AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.add_story)
        }

        if (savedInstanceState != null) {
            currentImageUri = addStoryViewModel.currentImageUri
            binding.descriptionEditText.setText(addStoryViewModel.description)
            showImage()
            showLoading()
        }
        setupButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        addStoryViewModel.saveInstanceState(currentImageUri, binding.descriptionEditText.text.toString())
    }

    private fun setupButton() {
        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.cameraButton.setOnClickListener {
            startCamera()
        }
        binding.uploadButton.setOnClickListener {
            uploadStory()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
            uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showToast()
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
            showToast()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadStory() {
        showLoading()
        val description = binding.descriptionEditText.text.toString()
        addStoryViewModel.getUser().observe(this) {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo", imageFile.name, requestImageFile
                )
                Log.d("Image File", "showImage: ${imageFile.path}")
                postData(multipartBody, description)
            } ?: showToast()
        }
    }

    private fun postData(file: MultipartBody.Part, description: String) {
        val descriptionRequestBody = description.toRequestBody(MultipartBody.FORM)

        lifecycleScope.launch {
            val apiService = addStoryViewModel.getApiServiceWithToken()

            if (apiService != null) {
                addStoryViewModel.postStory(apiService, file, descriptionRequestBody)
            }
        }

        addStoryViewModel.addStory.observe(this) {
            if (!it.error) {
                moveToMainActivity()
            }
        }
        showToast()
    }

    private fun moveToMainActivity() {
        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        addStoryViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showToast(){
        addStoryViewModel.toastText.observe(this){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}