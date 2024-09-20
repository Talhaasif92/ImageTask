package com.devfast.imagetask.screens

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.model.PhotosItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

@Composable
fun ViewPagerScreen(navController: NavHostController, imageViewModel: ImageViewModel, imageIndex: Int) {
    var currentIndex by remember { mutableIntStateOf(imageIndex) }
    val images by imageViewModel.images.collectAsState()
    val numPages = images.size
    val pagerState = rememberPagerState(initialPage = imageIndex) { numPages }

    var selectedFilter by remember { mutableStateOf("None") }
    var filteredImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    // Get CoroutineScope
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                filteredImage?.let {
                    imageViewModel.saveImage(context, it, "FilteredImage_${currentIndex + 1}.jpg")
                }
            } else {
                Toast.makeText(context, "Permission denied, cannot save image", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "${currentIndex + 1}/$numPages",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { selectedFilter = "None" },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
            ) {
                Text(
                    "No-Filter",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            Button(
                onClick = { selectedFilter = "Sepia" },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
            ) {
                Text(
                    "Sepia",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            Button(
                onClick = { selectedFilter = "Grayscale" },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
            ) {
                Text(
                    "Grayscale",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            Button(
                onClick = { selectedFilter = "Vintage" },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
            ) {
                Text(
                    "Vintage",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            var isImageLoaded by remember { mutableStateOf(false) }
            var filteredImage by remember { mutableStateOf<ImageBitmap?>(null) }

            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
            ) {
                LaunchedEffect(selectedFilter, images[page]) {
                    if (filteredImage == null || page != currentIndex) {
                        Log.d("showData", "show data")
                        filteredImage =  imageViewModel.applyFilter(
                            context = context,
                            imageUrl = images[page]?.src?.original,
                            filterType = selectedFilter
                        )

                        currentIndex = page
                        isImageLoaded = true
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    filteredImage?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "Filtered Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (!isImageLoaded) {
                        CircularProgressIndicator(modifier = Modifier.size(100.dp))
                    }
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        coroutineScope.launch {
                            // Use filteredImage if a filter is applied, else use the original image
                            val bitmapToSave = filteredImage ?: images[currentIndex]?.src?.original?.let {
                                imageViewModel.getBitmapFromUrl(context, it)
                            }
                            bitmapToSave?.let {
                                imageViewModel.saveImage(context, it, "Image_${currentIndex + 1}.jpg")
                            } ?: Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    "Save",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        // Use filteredImage if a filter is applied, else use the original image
                        val bitmapToCompress = filteredImage ?: images[currentIndex]?.src?.original?.let {
                            imageViewModel.getBitmapFromUrl(context, it)
                        }
                        bitmapToCompress?.let {
                            imageViewModel.compressImage(context, it, "CompressedImage_${currentIndex + 1}.jpg")
                        } ?: Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    "Compress",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                        // Launch permission request if necessary
                        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        coroutineScope.launch {
                            images.forEachIndexed { index, imageItem ->
                                // Handle each image in the list, saving either filteredImage or original image
                                val bitmapToSave = filteredImage ?: imageItem?.src?.original?.let {
                                    imageViewModel.getBitmapFromUrl(context, it)
                                }

                                // Save the bitmap if it exists
                                bitmapToSave?.let {
                                    imageViewModel.saveImage(context, it, "FilteredImage_${index + 1}.jpg")
                                } ?: Toast.makeText(context, "Image not found at index $index", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    "Save-All",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        images.forEachIndexed { index, imageItem ->
                            // Handle each image in the list, compressing either filteredImage or original image
                            val bitmapToCompress = filteredImage ?: imageItem?.src?.original?.let {
                                imageViewModel.getBitmapFromUrl(context, it)
                            }

                            // Compress the bitmap if it exists
                            bitmapToCompress?.let {
                                imageViewModel.compressImage(context, it, "CompressedImage_${index + 1}.jpg")
                            } ?: Toast.makeText(context, "Image not found at index $index", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    "Compress-All",
                    maxLines = 1,
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

        }

    }
}