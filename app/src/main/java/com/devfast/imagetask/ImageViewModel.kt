package com.devfast.imagetask

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.devfast.imagetask.model.PhotosItem
import com.devfast.imagetask.networking.ImageApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream

class ImageViewModel : ViewModel() {
    // Cache to store previously filtered images
    val imageCache = mutableMapOf<Pair<String?, String>, Bitmap?>()
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
    fun fetchImages(query: String = "house", perPage: Int = 80,page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ImageApiClient.imageService.searchPhotos(query, perPage, page = page)
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

    suspend fun applyFilter(context: Context, imageUrl: String?, filterType: String): ImageBitmap {
        // Check cache first to avoid reprocessing the image with the same filter
        val cacheKey = Pair(imageUrl, filterType)
        imageCache[cacheKey]?.let {
            return it.asImageBitmap() // Return cached image if already processed
        }

        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap

        val filteredBitmap = withContext(Dispatchers.Default) {
            when (filterType) {
                "Sepia" -> applySepiaFilter(bitmap)
                "Grayscale" -> applyGrayscaleFilter(bitmap)
                "Vintage" -> applyVintageFilter(bitmap)
                else -> bitmap
            }
        }

        // Store the processed image in the cache for future reuse
        imageCache[cacheKey] = filteredBitmap

        return filteredBitmap.asImageBitmap()
    }

    fun applySepiaFilter(source: Bitmap): Bitmap {
        val sepiaMatrix = ColorMatrix().apply {
            setScale(1f, 0.95f, 0.82f, 1f)
        }
        return applyColorMatrixFilter(source, sepiaMatrix)
    }

    fun applyGrayscaleFilter(source: Bitmap): Bitmap {
        val grayscaleMatrix = ColorMatrix().apply {
            setSaturation(0f)
        }
        return applyColorMatrixFilter(source, grayscaleMatrix)
    }

    fun applyVintageFilter(source: Bitmap): Bitmap {
        val vintageMatrix = ColorMatrix().apply {
            setScale(1f, 0.9f, 0.75f, 1f)
        }
        return applyColorMatrixFilter(source, vintageMatrix)
    }

    fun applyColorMatrixFilter(source: Bitmap, colorMatrix: ColorMatrix): Bitmap {
        val softwareBitmap = source.copy(Bitmap.Config.ARGB_8888, true)

        val filteredBitmap = Bitmap.createBitmap(softwareBitmap.width, softwareBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(filteredBitmap)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        canvas.drawBitmap(softwareBitmap, 0f, 0f, paint)
        return filteredBitmap
    }

    fun saveImage(context: Context, bitmap: Bitmap?, fileName: String) {
        if (bitmap == null) return
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
        outputStream?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        Toast.makeText(context, "Image Saved Successfully", Toast.LENGTH_SHORT).show()
    }

    fun compressImage(context: Context, bitmap: Bitmap?, fileName: String) {
        if (bitmap == null) return
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
        outputStream?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it) }
        Toast.makeText(context, "Image Compressed Successfully", Toast.LENGTH_SHORT).show()
    }

    suspend fun getBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
        val result = (loader.execute(request) as SuccessResult).drawable
        return (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
    }

    fun loadOriginalImage(context: Context, filePath: String?): Bitmap? {
        return if (filePath != null) {
            val imageFile = File(filePath)
            if (imageFile.exists()) {
                BitmapFactory.decodeFile(imageFile.absolutePath)  // Load the image as Bitmap
            } else {
                null  // Return null if file doesn't exist
            }
        } else {
            null  // Return null if filePath is null
        }
    }

}
