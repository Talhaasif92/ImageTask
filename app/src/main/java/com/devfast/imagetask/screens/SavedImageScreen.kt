package com.devfast.imagetask.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.devfast.imagetask.ImageViewModel

@Composable
fun SavedImageScreen(imageViewModel: ImageViewModel, navController: NavHostController) {
    // Collecting images from the StateFlow
    val savedImages = imageViewModel.images.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn {
            items(savedImages.filterNotNull()) { photoItem ->
                Image(
                    painter = rememberImagePainter(photoItem.src?.landscape), // Adjust this according to your PhotosItem
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}
