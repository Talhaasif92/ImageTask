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
import com.devfast.imagetask.screens.ApiImagesTab
import com.devfast.imagetask.screens.CameraImagesTab
import com.devfast.imagetask.screens.SavedImagesTab
import com.devfast.imagetask.screens.StorageImagesTab
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
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Button(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }) {
                    Text(text = "Back")
                }

                Button(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }) {
                    Text(text = "Next")
                }
            }

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
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                }
            }
        }
}