package com.soap.libms.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    content: @Composable() () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) {
        darkScheme
    } else {
        lightScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}