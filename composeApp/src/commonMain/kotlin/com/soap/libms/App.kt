package com.soap.libms

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun App(windowSizeClass: WindowSizeClass) {
    MaterialTheme {
        MainWidget(modifier = Modifier.fillMaxSize(), page = Page.BORROW, windowSizeClass = windowSizeClass)
    }
}

@Composable
fun MainWidget(modifier: Modifier = Modifier, page: Page, windowSizeClass: WindowSizeClass) {
    when (page) {
        Page.BORROW -> BorrowPage(modifier = modifier, windowSizeClass = windowSizeClass)
        Page.RETURN -> ReturnPage(modifier = modifier, windowSizeClass = windowSizeClass)
        Page.LOGIN -> LoginPage(modifier = modifier)
    }
}