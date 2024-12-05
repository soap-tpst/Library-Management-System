package com.soap.libms

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ApplicationTest {
    init {
        Database.connect("jdbc:sqlite:library-database.sqlite", driver = "org.sqlite.JDBC")
    }
    // ... rest of your test cases


    @Test
    fun `test root endpoint returns Ktor`() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Ktor", bodyAsText())
        }
    }

    @Test
    fun `test user registration with valid credentials`() = testApplication {
        application {
            module()
        }
        client.post("/users/add") {
            setBody(listOf(
                "username" to "newTestUser",
                "password" to "securePass123"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User added", bodyAsText())
        }

        // Add second user
        client.post("/users/add") {
            setBody(listOf(
                "username" to "secondUser",
                "password" to "pass456"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User added", bodyAsText())
        }
    }

    @Test
    fun `test user registration fails with missing credentials`() = testApplication {
        application {
            module()
        }
        client.post("/users/add") {
            setBody(listOf("username" to "testUser").formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Missing password", bodyAsText())
        }
    }

    @Test
    fun `test user login with valid credentials and borrow`() = testApplication {
        application {
            module()
        }

        transaction {
            Users.deleteAll()
            Items.deleteAll()
        }

        // Register user
        client.post("/users/add") {
            setBody(listOf(
                "username" to "testUser123",
                "password" to "testPass123"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User added", bodyAsText())
        }

        client.post("/items/add") {
            setBody(listOf(
                "title" to "Complete Guide to Kotlin",
                "ISBN" to "978-0-123456-78-9",
                "type" to "book",
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }



        var id = 0

        client.get("/items/search?query=Complete Guide to Kotlin").apply {
            val response = Json.decodeFromString<List<ItemJson>>(bodyAsText())
            if (response.isNotEmpty()) {
                val item = response[0]
                id = item.id
                assertTrue(id > 0)
            } else {
                id = 0
                println("No items found")
            }
        }

        client.post("/items/borrow") {
            setBody(listOf(
                "id" to id.toString(),
                "username" to "testUser123"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        // Login
        client.post("/users/login") {
            setBody(listOf(
                "username" to "testUser123",
                "password" to "testPass123"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(Json.decodeFromString<List<ItemJson>>(bodyAsText())[0].title, "Complete Guide to Kotlin")
        }
    }

    @Test
    fun `test adding new item`() = testApplication {
        application {
            module()
        }
        client.post("/items/add") {
            setBody(listOf(
                "title" to "Complete Guide to Kotlin",
                "ISBN" to "978-0-123456-78-9",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Item added", bodyAsText())
        }
    }

    @Test
    fun `test searching existing item`() = testApplication {
        application {
            module()
        }
        // Add item first
        client.post("/items/add") {
            setBody(listOf(
                "title" to "Complete Guide to Kotlin",
                "ISBN" to "978-0-123456-78-9",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        // Test search
        client.get("/items/search?query=Complete Guide to Kotlin").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Complete Guide to Kotlin"))
            assertTrue(bodyAsText().contains("978-0-123456-78-9"))
        }
    }

    @Test
    fun `test borrowing an item`() = testApplication {
        application {
            module()
        }
        // Add item first
        client.post("/items/add") {
            setBody(listOf(
                "title" to "Test Book",
                "ISBN" to "123-4-567890-12-3",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        // Test borrow
        client.post("/items/borrow") {
            setBody(listOf(
                "id" to "1",
                "username" to "libraryUser"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun `test returning a borrowed item`() = testApplication {
        application {
            module()
        }
        // Setup: Add and borrow item
        client.post("/items/add") {
            setBody(listOf(
                "title" to "Return Test Book",
                "ISBN" to "123-4-567890-12-3",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        client.post("/items/borrow") {
            setBody(listOf(
                "id" to "1",
                "username" to "libraryUser"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        // Test return
        client.post("/items/return") {
            setBody(listOf("id" to "1").formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }


    @Test
    fun `test search returns not found for non-existent items`() = testApplication {
        application {
            module()
        }
        client.get("/items/search?query=NonExistentBook").apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("No items found", bodyAsText())
        }
    }
}
