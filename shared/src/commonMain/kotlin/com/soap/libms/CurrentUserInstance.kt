package com.soap.libms



object CurrentUserInstance {
    var isLoggedIn = false
    var currentUser: User? = null

    fun login(username: String, password: String): Boolean {
        // TODO: Implement login logic
        isLoggedIn = true
        currentUser = createUser(username, password)
        return true
    }

    private fun createUser(username: String, password: String): User {
        return User(username, password)
    }
}