package com.soap.libms

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class User(val username: String, val password: String) {
    var borrowedItems = mutableListOf<BorrowedItem>()

    suspend fun fetchUserData(username: String, password: String): Boolean {
        borrowedItems.clear()

        val client = HttpClient()

        val response: HttpResponse = client.post("http://${Host.host}:${Host.port}/users/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(listOf("username" to username, "password" to getSha256Hash(password)).formUrlEncode())
        }

        if (response.status == HttpStatusCode.OK) {
            Json.decodeFromString<List<ItemJson>>(response.bodyAsText()).forEach {
                borrowedItems.add(BorrowedItem(it.id, it.title, it.ISBN, it.type, it.borrowedDate!!, it.dueDate!!))
            }
            return true
        }
        return false
    }
}