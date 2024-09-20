package com.devfast.imagetask.navigation


import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.devfast.imagetask.ImageViewModel
import com.devfast.imagetask.screens.ApiScreen
import com.devfast.imagetask.screens.CameraScreen
import com.devfast.imagetask.screens.ImageEditScreen
import com.devfast.imagetask.screens.ImageListScreen
import com.devfast.imagetask.screens.SavedImageScreen
import com.devfast.imagetask.screens.StorageScreen
import com.devfast.imagetask.screens.ViewPagerScreen

@Composable
fun ImageNavigation(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: Screen.ApiScreen.route

    val navigationActions = remember(navController) {
        ImageNavigationActions(navController)
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            BottomNavHost(
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            val isShowBottomNvBar = selectedDestination == Screen.ApiScreen.route || selectedDestination== Screen.CameraScreen.route || selectedDestination == Screen.StorageScreen.route || selectedDestination == Screen.SaveImageScreen.route
            if (isShowBottomNvBar) {
                BottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigationActions::navigateTo
                )
            }
        }
    }
}

@Composable
private fun BottomNavHost(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    val imageViewModel: ImageViewModel = viewModel()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.ApiScreen.route,
    ) {
        composable(Screen.ApiScreen.route) {
            ApiScreen(imageViewModel = imageViewModel, navController = navController)
        }
        composable(Screen.StorageScreen.route) {
            StorageScreen(imageViewModel = imageViewModel, navController = navController)
        }


        composable(
            route = Screen.ImageListScreen.route + "/{folderPath}",
            arguments = listOf(navArgument("folderPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val folderPath = Uri.decode(backStackEntry.arguments?.getString("folderPath") ?: "")
            ImageListScreen(folderPath = folderPath ?: "", navController = navController)
        }
        composable(Screen.CameraScreen.route) {
            CameraScreen(imageViewModel = imageViewModel, navController = navController)
        }
        composable(Screen.SaveImageScreen.route) {
            SavedImageScreen(imageViewModel = imageViewModel, navController = navController)
        }
        composable(
            route = Screen.ViewPagerScreen.route + "/{imageIndex}",
            arguments = listOf(navArgument("imageIndex"){type = NavType.IntType})
        ) {
            val imageIndex = it.arguments?.getInt("imageIndex")
            ViewPagerScreen(imageViewModel = imageViewModel, navController = navController, imageIndex = imageIndex ?: 0)
        }

        composable(
            route = Screen.ImageEditScreen.route + "/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = Uri.decode(backStackEntry.arguments?.getString("filePath") ?: "")
            ImageEditScreen(imageViewModel = imageViewModel, navController = navController, filePath = filePath ?: "")
        }


    }
}