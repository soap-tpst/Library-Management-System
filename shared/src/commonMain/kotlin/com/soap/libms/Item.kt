package com.soap.libms

open class Item(
    val id: Int,
    val title: String,
    val ISBN: String,
    val type: String,
    val isBorrowed: Boolean
) {
    fun borrow() {
        if (isBorrowed) {
            throw IllegalStateException("Book is already borrowed")
        }
        // TODO: Implement borrowing logic
    }
}