package com.soap.libms

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.json.Json

object Search {
    suspend fun search(query: String): List<Item> {
        val client = HttpClient()

        try {
            client.get("http://${Host.host}:${Host.port}/items/search?query=${query.encodeURLParameter()}").apply {
                val results = mutableListOf<Item>()
                if (status == HttpStatusCode.OK) {
                    val response = Json.decodeFromString<List<ItemJson>>(bodyAsText())
                    if (response.isNotEmpty()) {
                        response.forEach {
                            results.add(Item(it.id, it.title, it.ISBN, it.type))
                        }
                    }
                }
                return results
            }
        } catch (e: Exception) {
            println(e)
        } finally {
            client.close()
        }
        return mutableListOf<Item>()
    }
}