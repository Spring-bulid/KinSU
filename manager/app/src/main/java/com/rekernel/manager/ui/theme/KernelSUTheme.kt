package com.rekernel.manager.ui.theme

import androidx.compose.runtime.Composable
import com.rekernel.manager.ui.UiMode
import com.rekernel.manager.ui.LocalUiMode

@Composable
fun KernelSUTheme(
    appSettings: AppSettings = AppSettings(),
    uiMode: UiMode = UiMode.Miuix,
    content: @Composable () -> Unit
) {
    when (uiMode) {
        UiMode.Material -> MaterialKernelSUTheme(appSettings = appSettings, content = content)
        UiMode.Miuix -> MiuixKernelSUTheme(appSettings = appSettings, content = content)
    }
}