package com.devfast.imagetask.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.devfast.imagetask.ImageViewModel

@Composable
fun ImageEditScreen(navController: NavHostController, imageViewModel: ImageViewModel,filePath: String?) {

    Column {
        AsyncImage(model = filePath, contentDescription ="image" , modifier = Modifier
            .fillMaxSize())
    }
}