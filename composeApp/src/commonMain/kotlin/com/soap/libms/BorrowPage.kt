package com.soap.libms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun BorrowPage(modifier: Modifier, windowSizeClass: WindowSizeClass) {
    var searchResults by remember { mutableStateOf(listOf<Item>()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                    searchResults = search(newValue.text)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") }
            )
            SearchResultsTable(
                items = searchResults,
                modifier = Modifier.weight(1f).fillMaxSize(),
                windowSizeClass = windowSizeClass
            )
        }
    }
}

@Composable
fun SearchResultsTable(items: List<Item>, modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass) {
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
                        onClick = { item.borrow() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Borrow")
                    }
                }
            }
        }
    }
}