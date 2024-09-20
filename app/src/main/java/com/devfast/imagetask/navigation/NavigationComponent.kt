package com.devfast.imagetask.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devfast.imagetask.model.BottomNavigationItem

@Composable
fun BottomNavigationBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (BottomNavigationItem) -> Unit
) {

    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        BOTTOM_MENU_LIST.forEach { navBarDestination ->
            NavigationBarItem(
                selected = selectedDestination == navBarDestination.route,
                onClick = { navigateToTopLevelDestination(navBarDestination) },
                alwaysShowLabel = true,
                label = {
                    Text(text = navBarDestination.title)
                },
                icon = {
                    Image(
                        painter = painterResource(id = navBarDestination.selectedIcon),
                        contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }
}