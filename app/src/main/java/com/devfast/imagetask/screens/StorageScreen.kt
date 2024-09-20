package com.devfast.imagetask.screens

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class ImageFolder(val folderName: String, val folderPath: String, val coverImageUri: Uri)

@Composable
fun StorageScreen(imageViewModel: ImageViewModel, navController: NavHostController) {
    val context = LocalContext.current
    var imageFolders by remember { mutableStateOf(listOf<ImageFolder>()) }

    // Load the image folders asynchronously
    LaunchedEffect(Unit) {
        imageFolders = loadImageFolders(context)
    }

    // Display the folders in a grid
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(imageFolders.size) { index ->
            val folder = imageFolders[index]
            FolderThumbnail(folder) {
                val encodedUri = Uri.encode(folder.folderPath)
                navController.navigate(Screen.ImageListScreen.route + "/$encodedUri")  // Use encoded URI
            }
        }
    }
}

@Composable
fun FolderThumbnail(folder: ImageFolder, onClick: () -> Unit) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Load the cover image as bitmap
    LaunchedEffect(folder.coverImageUri) {
        bitmap = loadBitmapFromUri(context, folder.coverImageUri)
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
            )
        }
        Text(text = folder.folderName, maxLines = 1)
    }
}


// Function to load image folders and get their cover images
suspend fun loadImageFolders(context: Context): List<ImageFolder> {
    return withContext(Dispatchers.IO) {
        val folders = mutableMapOf<String, ImageFolder>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATA
        )
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
        cursor?.use {
            val bucketColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val folderName = cursor.getString(bucketColumnIndex)
                val imagePath = cursor.getString(dataColumnIndex)
                val folderPath = File(imagePath).parent ?: continue

                if (!folders.containsKey(folderPath)) {
                    val imageUri = Uri.fromFile(File(imagePath))
                    folders[folderPath] = ImageFolder(folderName, folderPath, imageUri)
                }
            }
        }

        folders.values.toList()
    }
}


// Helper function to load bitmap from URI using Coil
suspend fun loadBitmapFromUri(context: Context, uri: Uri): android.graphics.Bitmap? {
    return withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false) // Disable hardware bitmaps for better compatibility
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
    }
}
