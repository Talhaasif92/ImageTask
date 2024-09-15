package com.devfast.imagetask.model

import androidx.annotation.DrawableRes

data class BottomNavigationItem(
    val route: String,
    val title: String,
    @DrawableRes
    val selectedIcon: Int,
    @DrawableRes
    val unselectedIcon: Int,
)