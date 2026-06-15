package com.rekernel.manager.ui.component.bottombar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.rekernel.manager.ui.LocalUiMode
import com.rekernel.manager.ui.UiMode
import kotlinx.coroutines.launch

@Composable
fun BottomBar(pagerState: PagerState) {
    val uiMode = LocalUiMode.current
    val scope = rememberCoroutineScope()

    val items = listOf(
        Pair("Home", Icons.Outlined.Home),
        Pair("SuperUser", Icons.Outlined.Shield),
        Pair("Modules", Icons.Outlined.Widgets),
        Pair("Settings", Icons.Outlined.Settings),
    )

    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, (label, icon) ->
            NavigationBarItem(
                selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
            )
        }
    }
}