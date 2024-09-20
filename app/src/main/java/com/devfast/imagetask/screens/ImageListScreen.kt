package com.devfast.imagetask.screens

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ImageListScreen(folderPath: String, navController: NavHostController) {
    val context = LocalContext.current
    var images by remember { mutableStateOf(listOf<Uri>()) }

    // Load the images in the selected folder asynchronously
    LaunchedEffect(folderPath) {
        images = loadImagesFromFolder(context, folderPath)
    }

    // Display the images in a grid
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(images.size) { index ->
            val imageUri = images[index]
            ImageThumbnail(imageUri)
        }
    }
}

@Composable
fun ImageThumbnail(imageUri: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Load the image as bitmap
    LaunchedEffect(imageUri) {
        bitmap = loadBitmapFromUri(context, imageUri)
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(4.dp)
        )
    }
}

// Helper function to load all images from a folder
suspend fun loadImagesFromFolder(context: Context, folderPath: String): List<Uri> {
    return withContext(Dispatchers.IO) {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )
        val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("$folderPath%")
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            val dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(dataColumnIndex)
                val imageUri = Uri.fromFile(File(imagePath))
                images.add(imageUri)
            }
        }
        images
    }
}
