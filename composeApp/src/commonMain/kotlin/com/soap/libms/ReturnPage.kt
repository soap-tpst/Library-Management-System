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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
fun ItemSelection(items: List<BorrowedItem>, modifier: Modifier = Modifier, onValueChange: (BorrowedItem) -> Unit, selectedItemName: String) {
    var expanded by remember { mutableStateOf(false) }
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
            label = { Text("Select Item to Add") },
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
                        onValueChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReturnPage(modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass, shape: Shape = RectangleShape) {
    var selectedItem by remember { mutableStateOf<BorrowedItem?>(null) }
    val selectedItemsForReturn = remember { mutableStateListOf<BorrowedItem>() }
    var borrowedItems by remember { mutableStateOf(CurrentUserInstance.currentUser?.borrowedItems ?: mutableListOf<BorrowedItem>()) }
    var selectedItemName by remember { mutableStateOf(selectedItem?.title ?: "") }


    Surface(
        modifier = modifier,
        shape = shape
    ) {
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
                        onValueChange = {
                            selectedItem = it
                            selectedItemName = it.title
                                        },
                        selectedItemName = selectedItemName
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
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
                        onValueChange = {
                            selectedItem = it
                            selectedItemName = it.title
                                        },
                        selectedItemName = selectedItemName
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
                val buttonModifier = if (windowSizeClass == WindowSizeClass.COMPACT) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                }
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        selectedItemsForReturn.forEach {
                            CoroutineScope(Dispatchers.Default).launch {
                                if (it.returnItem()) {
                                    CurrentUserInstance.currentUser?.fetchUserData(
                                        CurrentUserInstance.currentUser?.username ?: "",
                                        CurrentUserInstance.currentUser?.password ?: ""
                                    )
                                    selectedItem = null
                                    selectedItemName = ""
                                    selectedItemsForReturn.remove(it)
                                    borrowedItems = CurrentUserInstance.currentUser?.borrowedItems ?: mutableListOf()
                                }
                            }
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

