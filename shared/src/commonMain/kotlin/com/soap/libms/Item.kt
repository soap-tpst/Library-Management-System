package com.soap.libms

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import kotlinx.serialization.Serializable

@Serializable
open class Item(
    val id: Int,
    val title: String,
    val ISBN: String,
    val type: String
) {
    suspend fun borrow(): Boolean {
        val client = HttpClient()
        client.post("http://${Host.host}:${Host.port}/items/borrow") {
            setBody(listOf(
                "id" to id.toString(),
                "username" to (CurrentUserInstance.currentUser?.username ?: "")
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            return status == HttpStatusCode.OK
        }
    }
}