package com.devfast.imagetask

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
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

    init {
        fetchImages()
    }


    // Add an image URI to the list
    fun addImage(photosItem: PhotosItem?) {
        _images.value += photosItem
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

    // Save an image to external storage
    fun saveImage(context: Context, uri: Uri): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ViewPagerImageApp")
        }

        val resolver = context.contentResolver
        val newUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(resolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(resolver, uri)
            }

            newUri?.let { outputUri ->
                resolver.openOutputStream(outputUri)?.use { outStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                }
            }

            newUri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Compress an image and save it to external storage
    fun compressImage(context: Context, uri: Uri): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "compressed_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ViewPagerImageApp")
        }

        val resolver = context.contentResolver
        val newUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(resolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(resolver, uri)
            }

            newUri?.let { outputUri ->
                resolver.openOutputStream(outputUri)?.use { outStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
                }
            }

            newUri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
