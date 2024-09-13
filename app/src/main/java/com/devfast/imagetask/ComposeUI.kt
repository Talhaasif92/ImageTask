package com.devfast.imagetask

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerScreen(imageViewModel: ImageViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val data = listOf("API", "Storage", "Camera", "Saved")
    val numPages = data.size
    val pagerState = rememberPagerState(0, 0F) { numPages }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.align(Alignment.Center)
        ) { page ->
            if (page in 0 until numPages) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (page) {
                        0 -> ApiImagesTab(imageViewModel)
                        1 -> StorageImagesTab(imageViewModel)
                        2 -> CameraImagesTab(imageViewModel)
                        3 -> SavedImagesTab(imageViewModel)
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }) {
                Text(text = "Next")
            }

//            Row {
//                repeat(numPages) {
//                    CustomIndicator(selected = pagerState.currentPage == it)
//                }
//            }
            Row {
                bottomNavigation()
            }
        }
    }
}

@Composable
fun bottomNavigation() {

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Modifier.fillMaxWidth().background(Color.Red)
                }
            }
        }
}

@Composable
fun ApiImagesTab(viewModel: ImageViewModel) {
    // Placeholder for API images
    Text(text = "API Images", modifier = Modifier.padding(16.dp))
}

@Composable
fun StorageImagesTab(viewModel: ImageViewModel) {
    // Placeholder for Storage images
    Text(text = "Storage Images", modifier = Modifier.padding(16.dp))
}

@Composable
fun CameraImagesTab(viewModel: ImageViewModel) {
    // Placeholder for Camera images
    Text(text = "Camera Images", modifier = Modifier.padding(16.dp))
}

@Composable
fun SavedImagesTab(viewModel: ImageViewModel) {
    // Placeholder for Saved images
    Text(text = "Saved Images", modifier = Modifier.padding(16.dp))
}


@Preview
@Composable
fun PreviewHorizontalPagerScreen() {
    HorizontalPagerScreen()
}