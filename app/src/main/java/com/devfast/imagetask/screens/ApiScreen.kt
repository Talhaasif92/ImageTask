package com.devfast.imagetask.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.R
import com.devfast.imagetask.model.PhotosItem
import com.devfast.imagetask.navigation.Screen

@Composable
fun ApiScreen(imageViewModel: ImageViewModel, navController: NavHostController) {
    val images = imageViewModel.images.collectAsState().value
    var isLoading by remember { mutableStateOf(true) }

    isLoading = images.isEmpty()

    if (isLoading) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_progress))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Once loaded, display the images in a grid with clickable functionality
        ImageList(
            images = images,
            onImageClick = {
                navController.navigate(Screen.ViewPagerScreen.route + "/$it") // Navigate to ViewPagerScreen
            },
            onSearchImage = {
                imageViewModel.fetchImages(it)
            }
        )
    }
}

@Composable
fun ImageList(
    images: List<PhotosItem?>,
    onImageClick: (Int) -> Unit,
    onSearchImage: (String) -> Unit
) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .border(width = 1.dp, color = Color.White),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchImage(searchQuery)
                    searchQuery = ""
                }
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = {
                items(images) { photo ->
                    AsyncImage(
                        model = photo?.src?.medium,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                val imageIndex = images.indexOf(photo) // Get index of clicked image
                                onImageClick(imageIndex)
                            }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )   
    }
}