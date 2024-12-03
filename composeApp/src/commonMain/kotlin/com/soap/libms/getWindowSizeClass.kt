package com.soap.libms

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun basedOnWidth(windowWidth: Dp): WindowSizeClass {
            return when {
                windowWidth < 600.dp -> COMPACT
                windowWidth < 840.dp -> MEDIUM
                else -> EXPANDED
            }
        }
    }
}