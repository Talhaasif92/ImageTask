package com.devfast.imagetask.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.R

@Composable
fun ViewPagerScreen(navController: NavHostController, imageViewModel: ImageViewModel, imageIndex: Int) {
    var currentIndex by remember { mutableIntStateOf(imageIndex) }
    val images by imageViewModel.images.collectAsState() // Use collectAsState to get the images
    val numPages = images.size
    val pagerState = rememberPagerState(initialPage = imageIndex) { numPages }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            var isImageLoaded by remember { mutableStateOf(false) }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(colorResource(id = R.color.white))
            ) {
                AsyncImage(
                    model = images[page]?.src?.medium,
                    contentDescription = images[page]?.alt,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .alpha(if (isImageLoaded) 1f else 0f),
                    contentScale = ContentScale.Crop,
                    onSuccess = { isImageLoaded = true }
                )

                if (!isImageLoaded) {
                    val composition by rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(
                            R.raw.loading_animation
                        )
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(100.dp) // Adjust size as needed
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { /* Save current image */ }) {
            Text("Save Image")
        }
        Button(onClick = { /* Compress current image */ }) {
            Text("Compress Image")
        }
        Button(onClick = { /* Save all images */ }) {
            Text("Save All Images")
        }
        Button(onClick = { /* Compress all images */ }) {
            Text("Compress All Images")
        }
    }

    LaunchedEffect(pagerState) {// Collect from pagerState.currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentIndex = page
        }
    }
}