package com.soap.libms

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState()
    var windowSizeClass by remember { mutableStateOf(WindowSizeClass.basedOnWidth(windowState.size.width)) }

    LaunchedEffect(windowState.size) {
        snapshotFlow { windowState.size }
            .collect { size: DpSize ->
                windowSizeClass = WindowSizeClass.basedOnWidth(size.width)
            }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Library-Management-System",
        state = windowState
    ) {
        App(windowSizeClass = windowSizeClass)
    }
}