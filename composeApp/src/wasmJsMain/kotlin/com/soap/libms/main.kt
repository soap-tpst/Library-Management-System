package com.soap.libms

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        var screenWidthDp by remember { mutableStateOf(window.innerWidth / window.devicePixelRatio) }
        var windowSizeClass by remember { mutableStateOf(WindowSizeClass.basedOnWidth(Dp(screenWidthDp.toFloat()))) }

        LaunchedEffect(window.innerWidth) {
            snapshotFlow { window.innerWidth }
                .collect { size: Int ->
                    windowSizeClass = WindowSizeClass.basedOnWidth(Dp((size / window.devicePixelRatio).toFloat()))
                }
        }

        App(windowSizeClass = WindowSizeClass.basedOnWidth(Dp(screenWidthDp.toFloat())))
    }
}