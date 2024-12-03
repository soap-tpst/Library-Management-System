package com.soap.libms

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun getWindowSizeClass(): WindowSizeClass {
    return when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> WindowSizeClass.COMPACT
        WindowWidthSizeClass.MEDIUM -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}