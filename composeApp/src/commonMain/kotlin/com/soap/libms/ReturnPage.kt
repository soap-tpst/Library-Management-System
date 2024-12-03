package com.soap.libms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun BorrowedBookItem(borrowedItem: BorrowedItem, onSelect: (BorrowedItem) -> Unit) {
    DropdownMenuItem(
        onClick = {
            onSelect(borrowedItem)
        },
        text = {
            Column(modifier = Modifier.padding(2.dp)) {
                Text(text = borrowedItem.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = "ID: ${borrowedItem.id}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Type: ${borrowedItem.type}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Due on: ${borrowedItem.dueDate}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSelection(items: List<BorrowedItem>, modifier: Modifier = Modifier, onValueChange: (BorrowedItem) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItemName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(expanded) {
        if (expanded) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItemName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .focusRequester(focusRequester),
            label = { Text("Item") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                BorrowedBookItem(
                    borrowedItem = item,
                    onSelect = {
                        selectedItemName = it.title
                        onValueChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReturnPage(modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass) {
    var selectedItem by remember { mutableStateOf<BorrowedItem?>(null) }
    val selectedItemsForReturn = remember { mutableStateListOf<BorrowedItem>() }
    // val borrowedItems = CurrentUserInstance.currentUser?.borrowedItems ?: mutableListOf<BorrowedItem>()
    val borrowedItems = mutableListOf<BorrowedItem>(
        BorrowedItem(
            id = 1,
            title = "The Great Gatsby",
            type = "Book",
            ISBN = "978-3-16-148410-0",
            isBorrowed = true,
            borrowedDate = "2021-10-01",
            dueDate = "2021-10-15"
        ),
    )

    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (windowSizeClass == WindowSizeClass.COMPACT) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ItemSelection(
                        items = borrowedItems,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { selectedItem = it }
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            selectedItem?.let {
                                if (selectedItemsForReturn.contains(it).not()) {
                                    selectedItemsForReturn.add(it)
                                } else {
                                    // TODO: Add prompt for user for duplicate entry
                                }
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ItemSelection(
                        items = borrowedItems,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        onValueChange = { selectedItem = it }
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            selectedItem?.let {
                                if (selectedItemsForReturn.contains(it).not()) {
                                    selectedItemsForReturn.add(it)
                                } else {
                                    // TODO: Add prompt for user for duplicate entry
                                }
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            BorrowedItemsTable(
                modifier = Modifier.weight(1f).fillMaxSize(),
                items = selectedItemsForReturn,
                windowSizeClass = windowSizeClass
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        selectedItemsForReturn.forEach {
                            it.returnItem()
                        }
                    }
                ) {
                    Text("Return")
                }
            }
        }
    }

}

@Composable
fun BorrowedItemsTable(items: List<BorrowedItem>, modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Title", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text("ID", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            if (windowSizeClass != WindowSizeClass.COMPACT) {
                Text("Type", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Text("Borrowed Date", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            }
            Text("Due Date", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        }
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.title, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.id.toString(), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (windowSizeClass != WindowSizeClass.COMPACT) {
                        Text(item.type, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.borrowedDate, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(item.dueDate, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

