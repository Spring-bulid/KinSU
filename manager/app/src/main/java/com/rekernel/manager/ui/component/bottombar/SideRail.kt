package com.rekernel.manager.ui.component.bottombar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rekernel.manager.ui.LocalUiMode
import com.rekernel.manager.ui.UiMode
import kotlinx.coroutines.launch

@Composable
fun SideRail(pagerState: PagerState) {
    val uiMode = LocalUiMode.current
    val scope = rememberCoroutineScope()

    val items = listOf(
        Triple("Home", Icons.Outlined.Home, "Home"),
        Triple("SuperUser", Icons.Outlined.Shield, "SuperUser"),
        Triple("Modules", Icons.Outlined.Widgets, "Modules"),
        Triple("Settings", Icons.Outlined.Settings, "Settings"),
    )

    NavigationRail(
        modifier = Modifier.fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        items.forEachIndexed { index, (label, icon, description) ->
            NavigationRailItem(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                icon = { Icon(icon, contentDescription = description) },
                label = { Text(label) },
            )
        }
    }
}