package com.devfast.imagetask.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
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
    var currentIndex by remember { mutableStateOf(imageIndex) }
    val images by imageViewModel.images.collectAsState()
    val numPages = images.size
    val pagerState = rememberPagerState(initialPage = imageIndex) { numPages }

    var selectedFilter by remember { mutableStateOf("None") }
    var filteredImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    // Get CoroutineScope
    val coroutineScope = rememberCoroutineScope()


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
            Button(onClick = { selectedFilter = "None" }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("No Filter")
            }
            Button(onClick = { selectedFilter = "Sepia" }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Sepia")
            }
            Button(onClick = { selectedFilter = "Grayscale" }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Grayscale")
            }
            Button(onClick = { selectedFilter = "Vintage" }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Vintage")
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
            ) {
                var isImageLoaded by remember { mutableStateOf(false) }

                LaunchedEffect(selectedFilter, images[page]) {
                    filteredImage = applyFilter(
                        context = context,
                        imageUrl = images[page]?.src?.original,
                        filterType = selectedFilter
                    )
                    isImageLoaded = true
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    filteredImage?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
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
            Button(onClick = {
                filteredImage?.let {
                    saveImage(context, it, "FilteredImage_${currentIndex + 1}.jpg")
                }
            }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Save Image")
            }
            Button(onClick = {
                filteredImage?.let {
                    compressImage(context, it, "CompressedImage_${currentIndex + 1}.jpg")
                }
            }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Compress Image")
            }
            Button(onClick = {
                // Launch a coroutine to handle saving all images
                coroutineScope.launch {
                    saveAllImages(context, images, "FilteredImage")
                }
            }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Save All Images")
            }
            Button(onClick = {
                // Launch a coroutine to handle compressing all images
                coroutineScope.launch {
                    compressAllImages(context, images, "CompressedImage")
                }
            }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text("Compress All Images")
            }
        }
    }
}

suspend fun applyFilter(context: Context, imageUrl: String?, filterType: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()
    val result = (loader.execute(request) as SuccessResult).drawable

    val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
    return withContext(Dispatchers.Default) {
        when (filterType) {
            "Sepia" -> applySepiaFilter(bitmap)
            "Grayscale" -> applyGrayscaleFilter(bitmap)
            "Vintage" -> applyVintageFilter(bitmap)
            else -> bitmap
        }
    }
}

fun applySepiaFilter(source: Bitmap): Bitmap {
    val sepiaMatrix = ColorMatrix().apply {
        setScale(1f, 0.95f, 0.82f, 1f)
    }
    return applyColorMatrixFilter(source, sepiaMatrix)
}

fun applyGrayscaleFilter(source: Bitmap): Bitmap {
    val grayscaleMatrix = ColorMatrix().apply {
        setSaturation(0f)
    }
    return applyColorMatrixFilter(source, grayscaleMatrix)
}

fun applyVintageFilter(source: Bitmap): Bitmap {
    val vintageMatrix = ColorMatrix().apply {
        setScale(1f, 0.9f, 0.75f, 1f)
    }
    return applyColorMatrixFilter(source, vintageMatrix)
}

fun applyColorMatrixFilter(source: Bitmap, colorMatrix: ColorMatrix): Bitmap {
    val softwareBitmap = source.copy(Bitmap.Config.ARGB_8888, true)

    val filteredBitmap = Bitmap.createBitmap(softwareBitmap.width, softwareBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(filteredBitmap)
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }
    canvas.drawBitmap(softwareBitmap, 0f, 0f, paint)
    return filteredBitmap
}

fun saveImage(context: Context, bitmap: Bitmap?, fileName: String) {
    if (bitmap == null) return
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    val outputStream: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
    outputStream?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
}

fun compressImage(context: Context, bitmap: Bitmap?, fileName: String) {
    if (bitmap == null) return
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    val outputStream: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
    outputStream?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it) }
}

suspend fun saveAllImages(context: Context, bitmaps: List<PhotosItem?>, fileNamePrefix: String) {
    bitmaps.forEachIndexed { index, photosItem ->
        val imageUrl = photosItem?.src?.original
        if (imageUrl != null) {
            val bitmap = getBitmapFromUrl(context, imageUrl)
            if (bitmap != null) {
                val fileName = "${fileNamePrefix}_$index.jpg"
                saveImage(context, bitmap, fileName)
            }
        }
    }
}

suspend fun compressAllImages(context: Context, bitmaps: List<PhotosItem?>, fileNamePrefix: String) {
    bitmaps.forEachIndexed { index, photosItem ->
        val imageUrl = photosItem?.src?.original
        if (imageUrl != null) {
            val bitmap = getBitmapFromUrl(context, imageUrl)
            if (bitmap != null) {
                val fileName = "${fileNamePrefix}_$index.jpg"
                compressImage(context, bitmap, fileName)
            }
        }
    }
}

suspend fun getBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()
    val result = (loader.execute(request) as SuccessResult).drawable
    return (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
}
