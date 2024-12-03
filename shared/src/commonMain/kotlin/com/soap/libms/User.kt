package com.soap.libms

class User(val username: String, private val password: String) {
    var borrowedItems = mutableListOf<BorrowedItem>()
    var availableItems = mutableListOf<Item>()

    fun fetchUserData() {
        TODO()
    }

    init {
        fetchUserData()
    }
}