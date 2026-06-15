package me.weishu.follkernel.ui.viewmodel

import androidx.compose.runtime.Immutable
import me.weishu.follkernel.ui.UiMode
import me.weishu.follkernel.ui.theme.AppSettings

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val uiMode: UiMode,
)
