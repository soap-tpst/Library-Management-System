package com.soap.libms

fun search(query: String): List<Item> {
    // TODO
    return when(query) {
        "The Great Gastby" -> listOf(
            Item(
                id = 1,
                title = "The Great Gatsby",
                type = "Book",
                ISBN = "978-3-16-148410-0",
                isBorrowed = false
            )
        )
        else -> emptyList()
    }
}