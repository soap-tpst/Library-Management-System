package com.soap.libms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import com.soap.libms.WindowSizeClass.*
import com.soap.libms.theme.AppTheme
import kotlinx.coroutines.delay
import library_management_system.composeapp.generated.resources.Res
import library_management_system.composeapp.generated.resources.login
import library_management_system.composeapp.generated.resources.logout
import library_management_system.composeapp.generated.resources.menu_book
import library_management_system.composeapp.generated.resources.place_item
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(InternalResourceApi::class)
@Composable
fun App(windowSizeClass: WindowSizeClass) {
    AppTheme {
        var currentPage by remember { mutableStateOf(Page.BORROW) }
        var isLoggedIn by remember { mutableStateOf(CurrentUserInstance.isLoggedIn) }

        LaunchedEffect(Unit) {
            while (true) {
                isLoggedIn = CurrentUserInstance.isLoggedIn
                delay(500)
            }
        }

        when (windowSizeClass) {
            COMPACT -> Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    MainWidget(
                        modifier = Modifier.weight(1f),
                        page = currentPage,
                        windowSizeClass = windowSizeClass
                    )
                    NavigationBar(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ) {
                        NavigationBarItem(
                            selected = currentPage == Page.BORROW,
                            onClick = { currentPage = Page.BORROW },
                            label = { Text("Borrow") },
                            icon = { Icon(painter = painterResource(Res.drawable.menu_book), contentDescription = "Borrow") }
                        )
                        NavigationBarItem(
                            selected = currentPage == Page.RETURN,
                            onClick = { currentPage = Page.RETURN },
                            label = { Text("Return") },
                            icon = { Icon(painter = painterResource(Res.drawable.place_item), contentDescription = "Return") }
                        )
                        if (isLoggedIn){
                            NavigationBarItem(
                                selected = currentPage == Page.LOGIN,
                                onClick = { CurrentUserInstance.logout() },
                                label = { Text("Logout") },
                                icon = { Icon(painter = painterResource(Res.drawable.logout), contentDescription = "Logout") }
                            )
                        } else {
                            NavigationBarItem(
                                selected = currentPage == Page.LOGIN,
                                onClick = { currentPage = Page.LOGIN },
                                label = { Text("Login") },
                                icon = { Icon(painter = painterResource(Res.drawable.login), contentDescription = "Login") }
                            )
                        }
                    }
                }
            }
            MEDIUM -> Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        NavigationRailItem(
                            selected = currentPage == Page.BORROW,
                            onClick = { currentPage = Page.BORROW },
                            label = { Text("Borrow") },
                            icon = { Icon(painter = painterResource(Res.drawable.menu_book), contentDescription = "Borrow") }
                        )
                        NavigationRailItem(
                            selected = currentPage == Page.RETURN,
                            onClick = { currentPage = Page.RETURN },
                            label = { Text("Return") },
                            icon = { Icon(painter = painterResource(Res.drawable.place_item), contentDescription = "Return") }
                        )
                        Spacer(Modifier.weight(1f).fillMaxHeight())
                        if (isLoggedIn){
                            NavigationRailItem(
                                selected = currentPage == Page.LOGIN,
                                onClick = { CurrentUserInstance.logout() },
                                label = { Text("Logout") },
                                icon = { Icon(painter = painterResource(Res.drawable.logout), contentDescription = "Logout") }
                            )
                        } else {
                            NavigationRailItem(
                                selected = currentPage == Page.LOGIN,
                                onClick = { currentPage = Page.LOGIN },
                                label = { Text("Login") },
                                icon = { Icon(painter = painterResource(Res.drawable.login), contentDescription = "Login") }
                            )

                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    MainWidget(
                        modifier = Modifier.weight(1f),
                        page = currentPage,
                        windowSizeClass = windowSizeClass,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            EXPANDED -> Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val baselineShift = -0.1f
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        NavigationRailItem(
                            selected = currentPage == Page.BORROW,
                            onClick = { currentPage = Page.BORROW },
                            icon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(painter = painterResource(Res.drawable.menu_book), contentDescription = "Borrow")
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        modifier = Modifier,
                                        text = "Borrow",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            baselineShift = BaselineShift(baselineShift)
                                        )
                                    )
                                }
                            }
                        )
                        NavigationRailItem(
                            selected = currentPage == Page.RETURN,
                            onClick = { currentPage = Page.RETURN },
                            icon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(painter = painterResource(Res.drawable.place_item), contentDescription = "Return")
                                    Spacer(Modifier.width(16.dp))
                                    Text("Return",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            baselineShift = BaselineShift(baselineShift)
                                        )
                                    )
                                }
                            }
                        )
                        Spacer(Modifier.weight(1f).fillMaxHeight())
                        if (isLoggedIn){
                            NavigationRailItem(
                                selected = currentPage == Page.LOGIN,
                                onClick = { CurrentUserInstance.logout() },
                                label = { Text("Logout") },
                                icon = {
                                    Icon(painter = painterResource(Res.drawable.logout), contentDescription = "Logout")
                                    Text("Logout",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            baselineShift = BaselineShift(baselineShift)
                                        )
                                    )
                                }
                            )
                        } else {
                            NavigationRailItem(
                                selected = currentPage == Page.LOGIN,
                                onClick = { currentPage = Page.LOGIN },
                                icon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(painter = painterResource(Res.drawable.login), contentDescription = "Login")
                                        Spacer(Modifier.width(16.dp))
                                        Text("Login",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                baselineShift = BaselineShift(baselineShift)
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    MainWidget(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        page = currentPage,
                        windowSizeClass = windowSizeClass,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainWidget(modifier: Modifier = Modifier, page: Page, windowSizeClass: WindowSizeClass, shape: Shape = RectangleShape) {
    when (page) {
        Page.BORROW -> BorrowPage(modifier = modifier, windowSizeClass = windowSizeClass, shape = shape)
        Page.RETURN -> ReturnPage(modifier = modifier, windowSizeClass = windowSizeClass, shape = shape)
        Page.LOGIN -> LoginPage(modifier = modifier, windowSizeClass = windowSizeClass, shape = shape)
    }
}