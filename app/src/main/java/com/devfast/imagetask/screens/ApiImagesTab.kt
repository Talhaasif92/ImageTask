package com.devfast.imagetask.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.networking.ImageApiClient
import com.devfast.imagetask.model.PhotosItem


@Composable
fun ApiImagesTab(imageViewModel: ImageViewModel) {
    var images by remember { mutableStateOf<List<PhotosItem?>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = ImageApiClient.imageService.searchPhotos("car",80)
            images = response.body()?.photos ?: emptyList()
            Log.d("ApiImagesTab", "Images: $images")
        } catch (e: Exception) {
            Log.d("ApiImagesTab E", "e: ${e.message}")

            // Handle error
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator() // Example loading indicator
    } else {
        ImageList(images)
    }
}

@Composable
fun ImageList(images: List<PhotosItem?>) {
    LazyColumn {
        items(images) { image ->
            image?.let {
                AsyncImage(
                    model = it.src?.landscape, // Access medium from Src
                    contentDescription = it.alt,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}



