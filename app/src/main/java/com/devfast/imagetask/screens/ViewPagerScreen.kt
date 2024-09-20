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
                    saveImage(context, it, "FilteredImage_${currentIndex + 1}.jpg")
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
                        filteredImage = applyFilter(
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
                            val bitmapToSave = filteredImage ?: images[currentIndex]?.src?.original?.let {
                                getBitmapFromUrl(context,
                                    it
                                )
                            }
                            bitmapToSave?.let {
                                saveImage(context, it, "Image_${currentIndex + 1}.jpg")
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
                    style = TextStyle(fontSize = 12.sp) // Adjust as necessary
                )
            }

            Button(
                onClick = {
                    filteredImage?.let {
                        compressImage(context, it, "CompressedImage_${currentIndex + 1}.jpg")
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
                        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {coroutineScope.launch {
                        saveAllImages(context, images, "FilteredImage")
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
                        compressAllImages(context, images, "CompressedImage")
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
// Cache to store previously filtered images
val imageCache = mutableMapOf<Pair<String?, String>, Bitmap?>()

suspend fun applyFilter(context: Context, imageUrl: String?, filterType: String): ImageBitmap {
    // Check cache first to avoid reprocessing the image with the same filter
    val cacheKey = Pair(imageUrl, filterType)
    imageCache[cacheKey]?.let {
        return it.asImageBitmap() // Return cached image if already processed
    }

    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()

    val result = (loader.execute(request) as SuccessResult).drawable
    val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap

    val filteredBitmap = withContext(Dispatchers.Default) {
        when (filterType) {
            "Sepia" -> applySepiaFilter(bitmap)
            "Grayscale" -> applyGrayscaleFilter(bitmap)
            "Vintage" -> applyVintageFilter(bitmap)
            else -> bitmap
        }
    }

    // Store the processed image in the cache for future reuse
    imageCache[cacheKey] = filteredBitmap

    return filteredBitmap.asImageBitmap()
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
    Toast.makeText(context, "Image Saved Successfully", Toast.LENGTH_SHORT).show()
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
    Toast.makeText(context, "Image Compressed Successfully", Toast.LENGTH_SHORT).show()
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

    Toast.makeText(context, "Images Saved Successfully", Toast.LENGTH_SHORT).show()

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
    Toast.makeText(context, "Images Compressed Successfully", Toast.LENGTH_SHORT).show()
}

suspend fun getBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()
    val result = (loader.execute(request) as SuccessResult).drawable
    return (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
}