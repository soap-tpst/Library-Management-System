package com.soap.libms

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CurrentUserInstance {
    var isLoggedIn = false
    var currentUser: User? = null

    fun login(username: String, password: String) {
        currentUser = createUser(username, password)
    }

    private fun createUser(username: String, password: String): User {
        val user = User(username, password)
        CoroutineScope(Dispatchers.Default).launch {
            if (user.fetchUserData(username, password)) {
                isLoggedIn = true
            }
        }
        return user
    }

    fun logout() {
        isLoggedIn = false
        currentUser = null
    }
}