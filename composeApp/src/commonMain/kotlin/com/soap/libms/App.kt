package com.soap.libms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App(windowSizeClass: WindowSizeClass) {
    MaterialTheme {
        var currentPage by remember { mutableStateOf(Page.BORROW) }

        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier.padding(16.dp)
            ) {
                NavigationRailItem(
                    selected = currentPage == Page.BORROW,
                    onClick = { currentPage = Page.BORROW },
                    label = { Text("Borrow") },
                    icon = { }
                )
                NavigationRailItem(
                    selected = currentPage == Page.RETURN,
                    onClick = { currentPage = Page.RETURN },
                    label = { Text("Return") },
                    icon = { }
                )
            }
            MainWidget(
                modifier = Modifier.weight(1f),
                page = currentPage,
                windowSizeClass = windowSizeClass
            )
        }
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