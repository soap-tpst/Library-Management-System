package com.soap.libms

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class BorrowedItem(
    id: Int,
    title: String,
    ISBN: String,
    type: String,
    val borrowedDate: String,
    val dueDate: String
) : Item(id, title, ISBN, type) {
    suspend fun returnItem(): Boolean{
        val client = HttpClient()

        client.post("http://${Host.host}:${Host.port}/items/return") {
            setBody(listOf("id" to id.toString()).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            return status == HttpStatusCode.OK
        }
    }
}