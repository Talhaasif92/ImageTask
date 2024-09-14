package com.devfast.imagetask.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devfast.imagetask.ImageViewModel


@Composable
fun StorageImagesTab(viewModel: ImageViewModel, navController: NavHostController) {
    // Placeholder for Storage images
    Text(text = "Storage Images", modifier = Modifier.padding(16.dp))
}
