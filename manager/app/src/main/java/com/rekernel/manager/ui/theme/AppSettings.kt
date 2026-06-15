package com.rekernel.manager.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.materialkolor.dynamiccolor.ColorSpec

enum class ColorMode(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    MONET_SYSTEM("monet_system"),
    MONET_LIGHT("monet_light"),
    MONET_DARK("monet_dark"),
    DARK_AMOLED("dark_amoled");

    val isSystem: Boolean get() = this == SYSTEM || this == MONET_SYSTEM
    val isDark: Boolean get() = this == DARK || this == MONET_DARK || this == DARK_AMOLED
    val isAmoled: Boolean get() = this == DARK_AMOLED

    companion object {
        fun fromValue(value: String): ColorMode = entries.find { it.value == value } ?: SYSTEM
        val DEFAULT_VALUE = SYSTEM.value
    }
}

data class AppSettings(
    val colorMode: ColorMode = ColorMode.SYSTEM,
    val keyColor: Int = 0,
    val paletteStyle: com.materialkolor.PaletteStyle = com.materialkolor.PaletteStyle.TonalSpot,
    val colorSpec: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2021,
    val uiMode: String = UiModeValue.MIU_IX,
    val enableBlur: Boolean = true,
    val enableFloatingBottomBar: Boolean = true,
    val enableFloatingBottomBarBlur: Boolean = true,
    val pageScale: Float = 1.0f,
)

object UiModeValue {
    const val MIU_IX = "miuix"
    const val MATERIAL = "material"
}

val LocalColorMode = staticCompositionLocalOf { ColorMode.SYSTEM.value }
val LocalEnableBlur = staticCompositionLocalOf { true }
val LocalEnableFloatingBottomBar = staticCompositionLocalOf { true }
val LocalEnableFloatingBottomBarBlur = staticCompositionLocalOf { true }