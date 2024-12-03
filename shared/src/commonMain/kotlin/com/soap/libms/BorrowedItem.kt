package com.soap.libms

class BorrowedItem(
    id: Int,
    title: String,
    ISBN: String,
    type: String,
    isBorrowed: Boolean,
    val borrowedDate: String,
    val dueDate: String
) : Item(id, title, ISBN, type, isBorrowed) {
    fun returnItem() {
        TODO()
    }
}