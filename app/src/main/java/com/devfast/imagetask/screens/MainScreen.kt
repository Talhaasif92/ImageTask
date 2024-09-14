package com.devfast.imagetask.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(lifecycleOwner: LifecycleOwner) {
    val navController = rememberNavController()
    val activity: Activity = LocalContext.current as Activity

    NavHost(
        navController = navController,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        startDestination = "HomeScreen"
    ) {
        composable("HomeScreen") {
            HomeScreen()
        }

//        composable("settings") { _ ->
//            Scaffold(
//                topBar = {
//                    Log.d("Topbar","172")
//                    topBarRoomUser(navController = navController, name = "Settings")
//                }
//            ) {
//                SettingsScreen(navController = navController)
//            }
//        }

    }
}

