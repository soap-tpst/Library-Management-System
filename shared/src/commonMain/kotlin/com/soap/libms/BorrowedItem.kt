package com.soap.libms

class BorrowedItem(
    id: Int,
    title: String,
    ISBN: String,
    type: String,
    val borrowedDate: String,
    val dueDate: String
) : Item(id, title, ISBN, type) {
    fun returnItem() {
        TODO()
    }
}