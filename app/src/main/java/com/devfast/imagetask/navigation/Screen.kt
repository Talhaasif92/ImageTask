package com.devfast.imagetask.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.devfast.imagetask.R
import com.devfast.imagetask.model.BottomNavigationItem

sealed class Screen(val route: String) {
    data object ApiScreen : Screen("ApiScreen")
    data object StorageScreen : Screen("StorageScreen")
    data object CameraScreen : Screen("CameraScreen")
    data object SaveImageScreen : Screen("SaveImageScreen")
    data object ViewPagerScreen : Screen("ViewPagerScreen")
    data object ImageEditScreen : Screen("ImageEditScreen")
    data object ImageListScreen : Screen("image_list_screen/{folderPath}") {
        fun passFolderPath(folderPath: String): String {
            return "image_list_screen/$folderPath"
        }
    }
}

class ImageNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: BottomNavigationItem) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

val BOTTOM_MENU_LIST = listOf(
    BottomNavigationItem(
        title = "API",
        route = Screen.ApiScreen.route,
        selectedIcon = R.drawable.photo_download,
        unselectedIcon = R.drawable.photo_download
    ),
    BottomNavigationItem(
        title = "Storage",
        route = Screen.StorageScreen.route,
        selectedIcon = R.drawable.store_image,
        unselectedIcon = R.drawable.store_image
    ),
    BottomNavigationItem(
        title = "Camera",
        route = Screen.CameraScreen.route,
        selectedIcon = R.drawable.camera,
        unselectedIcon = R.drawable.camera
    ),
    BottomNavigationItem(
        title = "Saved",
        route = Screen.SaveImageScreen.route,
        selectedIcon = R.drawable.ic_folder,
        unselectedIcon = R.drawable.ic_folder
    ),
)