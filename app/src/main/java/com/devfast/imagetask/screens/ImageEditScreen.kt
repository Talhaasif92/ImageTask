package com.devfast.imagetask.screens

import androidx.compose.runtime.Composable
import com.devfast.imagetask.ImageViewModel

@Composable
fun ImageEditScreen(imageViewModel: ImageViewModel) {
    val selectedImageUrl = imageViewModel.selectedImageUrl.value

    // Use `selectedImageUrl` to load and edit the image
}
