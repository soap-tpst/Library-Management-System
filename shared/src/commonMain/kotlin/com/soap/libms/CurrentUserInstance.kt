package com.soap.libms

object CurrentUserInstance {
    var isLoggedIn = false
    var currentUser: User? = null
    var showDialog = false


    suspend fun login(username: String, password: String): Boolean {
        currentUser = User(username, password)
        isLoggedIn = currentUser?.fetchUserData(username, password) == true
        showDialog = true
        return isLoggedIn
    }

    fun logout() {
        isLoggedIn = false
        currentUser = null
    }
}