package com.devfast.imagetask.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.R
import com.devfast.imagetask.model.NavItemState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(imageViewModel: ImageViewModel = viewModel()) {
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()

    val items = listOf(
        NavItemState(title = "API", selectedIcon = R.drawable.photo_download),
        NavItemState(title = "Storage", selectedIcon = R.drawable.store_image),
        NavItemState(title = "Camera", selectedIcon = R.drawable.camera),
        NavItemState(title = "Saved", selectedIcon = R.drawable.camera)
    )

    val pagerState = rememberPagerState(0, 0F) { items.size }

    var bottomNavState by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(70.dp),
                containerColor = NavigationBarDefaults.containerColor
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        onClick = {
                            bottomNavState = index
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selected = bottomNavState == index,
                        alwaysShowLabel = false,
                        icon = {
                            Image(
                                painter = painterResource(id = item.selectedIcon),
                                contentDescription = item.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(text = item.title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = NavigationBarDefaults.containerColor
                        )
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> ApiImagesTab(imageViewModel, navController)
                    1 -> StorageImagesTab(imageViewModel, navController)
                    2 -> CameraImagesTab(imageViewModel, navController)
                    3 -> SavedImagesTab(imageViewModel, navController)
                }
            }
        }
    }
}