package com.soap.libms

object CurrentUserInstance {
    var isLoggedIn = false
    var currentUser: User? = null


    suspend fun login(username: String, password: String): Boolean {
        currentUser = User(username, password)
        isLoggedIn = currentUser?.fetchUserData(username, password) ?: false
        return isLoggedIn
    }

    fun logout() {
        isLoggedIn = false
        currentUser = null
    }
}