
package com.devfast.imagetask

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class ImageViewModel : ViewModel() {
    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    fun addImage(uri: Uri) {
        _images.value += uri
    }

    fun saveImage(context: Context, uri: Uri): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ViewPagerImageApp")
        }

        val resolver = context.contentResolver
        val newUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(resolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(resolver, uri)
            }

            newUri?.let { outputUri ->
                resolver.openOutputStream(outputUri).use { outStream ->
                    outStream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return newUri
    }

    fun compressImage(context: Context, uri: Uri): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "compressed_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ViewPagerImageApp")
        }

        val resolver = context.contentResolver
        val newUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(resolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(resolver, uri)
            }

            newUri?.let { outputUri ->
                resolver.openOutputStream(outputUri).use { outStream ->
                    outStream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return newUri
    }
}
