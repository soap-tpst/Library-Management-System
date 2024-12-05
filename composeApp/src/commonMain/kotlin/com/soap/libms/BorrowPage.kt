package com.soap.libms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BorrowPage(modifier: Modifier, windowSizeClass: WindowSizeClass, shape: Shape = RectangleShape) {
    var searchResults by remember { mutableStateOf(mutableListOf<Item>()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        searchResults = Search.search(searchQuery) as MutableList<Item>
    }

    Surface(
        modifier = modifier,
        shape = shape,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^[a-zA-Z0-9]*$"))) searchQuery = newValue
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") }
            )
            Spacer(Modifier.height(16.dp))
            SearchResultsTable(
                items = searchResults,
                modifier = Modifier.weight(1f).fillMaxSize(),
                windowSizeClass = windowSizeClass,
                onBorrow = { item ->
                    CoroutineScope(Dispatchers.Default).launch {
                        if (item.borrow()) {
                            searchQuery = ""
                            CurrentUserInstance.currentUser?.fetchUserData(
                                CurrentUserInstance.currentUser?.username ?: "",
                                CurrentUserInstance.currentUser?.password ?: ""
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SearchResultsTable(items: List<Item>, modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass, onBorrow: (Item) -> Unit = {}) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ID", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text("Title", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            if (windowSizeClass != WindowSizeClass.COMPACT) {
                Text("Type", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Text("ISBN", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            }
            Text("Borrow", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        }
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.id.toString(), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.title, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (windowSizeClass != WindowSizeClass.COMPACT) {
                        Text(item.type, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.ISBN, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Button(
                        onClick = {
                            onBorrow(item)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Borrow")
                    }
                }
            }
        }
    }
}