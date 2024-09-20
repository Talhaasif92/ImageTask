package com.devfast.imagetask.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.devfast.imagetask.ImageViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImageEditScreen(
    navController: NavHostController,
    imageViewModel: ImageViewModel,
    filePath: String?
) {

    var selectedFilter by remember { mutableStateOf("None") }
    var filteredImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    // Get CoroutineScope
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add padding for overall layout
        verticalArrangement = Arrangement.SpaceBetween // Space out the components
    ) {
        // Filter buttons at the top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Filter buttons (No-Filter, Sepia, etc.)
            Button(
                onClick = { selectedFilter = "None" },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
            ) {
                Text(
                    "No-Filter",
                    maxLines = 1, // Limit to one line
                    softWrap = true,
                    style = TextStyle(fontSize = 12.sp) // Set a base text size
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

        // Card displaying the filtered image
        Card(
            modifier = Modifier
                .weight(1f) // Allow the image to take up available space
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {


            LaunchedEffect(selectedFilter) {

                Log.d("showData", "show data")
                filteredImage = applyFilter(
                    context = context,
                    imageUrl = filePath,
                    filterType = selectedFilter
                ).asAndroidBitmap()
            }


            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (filePath != null) {
                    AsyncImage(
                        model = filteredImage,
                        contentDescription = "Filtered Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("No image selected", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
        // Buttons for Save and Compress at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    filteredImage?.let {
                        saveImage(context, it, "Edit_${createImageFileName()}.jpg")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text("Save", style = TextStyle(fontSize = 12.sp))
            }

            Button(
                onClick = {
                    filteredImage?.let {
                        val file = createImageFile(context)
                        compressImage(context, it, "Edit_${createImageFileName()}.jpg")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text("Compress", style = TextStyle(fontSize = 12.sp))
            }
        }
    }

}

fun createImageFileName(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "IMG_$timeStamp"  // Shorter file name based on timestamp
}