package com.devfast.imagetask

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devfast.imagetask.model.PhotosItem
import com.devfast.imagetask.networking.ImageApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ImageViewModel : ViewModel() {

    private val _images = MutableStateFlow<List<PhotosItem?>>(emptyList())
    val images: StateFlow<List<PhotosItem?>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var selectedImageUrl = mutableStateOf<String?>(null)

    init {
        fetchImages()
    }


    // Function to update the selected image URL
    fun setSelectedImageUrl(url: String) {
        selectedImageUrl.value = url
    }



    // Fetch images from the API
    fun fetchImages(query: String = "house") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ImageApiClient.imageService.searchPhotos(query, 80)
                val imageUris = response.body()?.photos

                if (imageUris != null) {
                    _images.value = imageUris
                }
                Log.d("ImageViewModel", "Images fetched: $imageUris")
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Error fetching images: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
