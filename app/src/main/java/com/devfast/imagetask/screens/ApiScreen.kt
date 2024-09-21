package com.devfast.imagetask.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.R
import com.devfast.imagetask.model.PhotosItem
import com.devfast.imagetask.navigation.Screen

@Composable
fun ApiScreen(imageViewModel: ImageViewModel, navController: NavHostController) {
    val images = imageViewModel.images.collectAsState().value
    var isLoading by remember { mutableStateOf(false) }
    val skeletonCount = 12

    LaunchedEffect(images) {
        isLoading = images.isEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        ImageSearchBar(
            onSearchImage = { query ->
                isLoading = true
                imageViewModel.fetchImages(query)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            SkeletonImageList(skeletonCount)
        } else {
            ImageList(
                images = images,
                onImageClick = {
                    navController.navigate(Screen.ViewPagerScreen.route + "/$it")
                }
            )
        }
    }
}

@Composable
fun SkeletonImageList(count: Int) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
        content = {
            items(count) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )
            }
        }
    )
}

@Composable
fun ImageSearchBar(onSearchImage: (String) -> Unit) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(width = 1.dp, color = Color.White),
        value = searchQuery,
        onValueChange = {
            searchQuery = it
        },
        placeholder = { Text(text = "Search Image") },
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
}

@Composable
fun ImageList(
    images: List<PhotosItem?>,
    onImageClick: (Int) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(images) { photo ->
                AsyncImage(
                    model = photo?.src?.medium ?: photo?.src?.small,
                    placeholder = painterResource(id = R.drawable.loading_image),
                    error = painterResource(id = R.drawable.image_error),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            val imageIndex = images.indexOf(photo)
                            onImageClick(imageIndex)
                        }
                )
            }
        },
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
    )
}