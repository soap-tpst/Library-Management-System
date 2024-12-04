package com.soap.libms

open class Item(
    val id: Int,
    val title: String,
    val ISBN: String,
    val type: String
) {
    fun borrow() {
        // TODO: Implement borrowing logic
    }
}