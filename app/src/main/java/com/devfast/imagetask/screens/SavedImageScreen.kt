package com.devfast.imagetask.screens

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SavedImageScreen(imageViewModel: ImageViewModel, navController: NavHostController) {
    // Collecting images from the StateFlow
//    val savedImages = imageViewModel.images.collectAsState().value
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        LazyColumn {
//            items(savedImages.filterNotNull()) { photoItem ->
//                Image(
//                    painter = rememberImagePainter(photoItem.src?.landscape), // Adjust this according to your PhotosItem
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .padding(bottom = 8.dp)
//                )
//            }
//        }
//    }
    val context = LocalContext.current
    val galleryImages = remember { mutableStateListOf<String>() }

    // Fetch images from the device storage
    LaunchedEffect(Unit) {
        val images = loadGalleryImages(context.contentResolver)
        galleryImages.addAll(images)
    }

    if (galleryImages.isNotEmpty()) {
        // Display images in a grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(galleryImages.size) { index ->
                val imageUrl = galleryImages[index]
                ImageThumbnail(imageUrl = imageUrl) {
                    val encodedUri = Uri.encode(imageUrl)  // Encode the URI
                    imageViewModel.setSelectedImageUrl(imageUrl)
                    navController.navigate(Screen.ImageEditScreen.route + "/$encodedUri")  // Use encoded URI
                }
            }
        }
    } else {
        // Show loading text if images are not loaded yet
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = "Loading images...", modifier = Modifier.padding(16.dp))
        }
    }
}

// Function to load images from the MediaStore
fun loadGalleryImages(contentResolver: ContentResolver): List<String> {
    val images = mutableListOf<String>()
    val projection = arrayOf(
        MediaStore.Images.Media.DATA, // Use _DATA for the file path
        MediaStore.Images.Media.DATE_ADDED
    )
    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null, // Selection
        null, // Selection args
        "${MediaStore.Images.Media.DATE_ADDED} DESC" // Sort by date added
    )
    cursor?.use {
        val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        while (it.moveToNext()) {
            val imagePath = it.getString(dataIndex)
            images.add(imagePath)
        }
    }
    return images
}

@Composable
fun ImageThumbnail(imageUrl: String, onClick: () -> Unit) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Load the image using Coil
    LaunchedEffect(imageUrl) {
        bitmap = loadImageBitmap(context, imageUrl)
    }

    // Display the image or a placeholder if not loaded yet
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Gallery Image",
            modifier = Modifier
                .size(120.dp)
                .padding(4.dp)
                .clickable { onClick() }
        )
    } else {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

// Function to load image bitmap using Coil
suspend fun loadImageBitmap(context: Context, imageUrl: String): android.graphics.Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()
    return withContext(Dispatchers.IO) {
        val result = (loader.execute(request) as SuccessResult).drawable
        (result as android.graphics.drawable.BitmapDrawable).bitmap
    }
}
