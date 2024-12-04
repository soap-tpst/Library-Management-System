import com.soap.libms.module
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Ktor", response.bodyAsText())
    }

    @Test
    fun testUserRegistration() = testApplication {
        application {
            module()
        }
        val response = client.post("/users/add") {
            setBody(listOf("username" to "testUser", "password" to "testPass").formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User added", response.bodyAsText())
    }

    @Test
    fun testUserLogin() = testApplication {
        application {
            module()
        }
        // First register the user
        client.post("/users/add") {
            setBody(listOf("username" to "loginUser", "password" to "loginPass").formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        // Then try to login
        val response = client.post("/users/login") {
            setBody(listOf("username" to "loginUser", "password" to "loginPass").formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testAddItem() = testApplication {
        application {
            module()
        }
        val response = client.post("/items/add") {
            setBody(listOf(
                "title" to "Test Book",
                "ISBN" to "123456789",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Item added", response.bodyAsText())
    }

    @Test
    fun testSearchItem() = testApplication {
        application {
            module()
        }
        // First add an item
        client.post("/items/add") {
            setBody(listOf(
                "title" to "Search Test Book",
                "ISBN" to "987654321",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        // Then search for it
        val response = client.get("/items/search?query=Search Test Book")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Search Test Book"))
    }

    @Test
    fun testBorrowAndReturnItem() = testApplication {
        application {
            module()
        }
        // Add an item first
        client.post("/items/add") {
            setBody(listOf(
                "title" to "Borrow Test Book",
                "ISBN" to "111222333",
                "type" to "book"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }

        // Borrow the item
        val borrowResponse = client.post("/items/borrow") {
            setBody(listOf(
                "id" to "1",
                "username" to "borrowUser"
            ).formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
        assertEquals(HttpStatusCode.OK, borrowResponse.status)

        // Return the item
        val returnResponse = client.post("/items/return") {
            setBody(listOf("id" to "1").formUrlEncode())
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        }
        assertEquals(HttpStatusCode.OK, returnResponse.status)
    }
}
