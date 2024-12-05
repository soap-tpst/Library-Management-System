package com.soap.libms

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    suspend fun receiveUsernameAndPassword(call: ApplicationCall): Pair<String, String>? {
        val params = call.receiveParameters()
        val username = params["username"] ?: run {
            call.respondText("Missing username", status = HttpStatusCode.BadRequest)
            return null
        }
        val password = params["password"] ?: run {
            call.respondText("Missing password", status = HttpStatusCode.BadRequest)
            return null
        }
        return Pair(username, password)
    }

    routing {
        get("/") {
            call.respondText("Ktor")
        }

        route("/users") {
            post("/add") {
                val (username, password) = receiveUsernameAndPassword(call) ?: return@post
                try {
                    transaction {
                        Users.insert {
                            it[Users.username] = username
                            it[Users.password] = password
                        }
                    }
                    call.respondText("User added")
                } catch (e: ExposedSQLException) {
                    if (e.sqlState == "23505") { // Unique violation
                        call.respondText("User already exists", status = HttpStatusCode.Conflict)
                    } else {
                        call.respondText("Database error", status = HttpStatusCode.InternalServerError)
                    }
                }
            }
            post("/login") {
                val (username, password) = receiveUsernameAndPassword(call) ?: return@post
                val userExists = transaction {
                    Users.selectAll().where((Users.username eq username) and (Users.password eq password))
                        .count() > 0
                }

                if (!userExists) {
                    call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
                } else {
                    val borrowedItems = transaction {
                        Items.selectAll()
                            .where ( Items.borrower eq username and Items.isBorrowed )
                            .map { it.toItemJson() }
                    }
                    call.respondText(Json.encodeToString(ListSerializer(ItemJson.serializer()), borrowedItems), status = HttpStatusCode.OK)
                }
            }
        }

        route("/items") {
            post("/add") {
                val params = call.receiveParameters()
                val title =
                    params["title"] ?: return@post call.respondText("Missing title", status = HttpStatusCode.BadRequest)
                val ISBN =
                    params["ISBN"] ?: return@post call.respondText("Missing ISBN", status = HttpStatusCode.BadRequest)
                val type =
                    params["type"] ?: return@post call.respondText("Missing type", status = HttpStatusCode.BadRequest)
                transaction {
                    Items.insert {
                        it[Items.title] = title
                        it[Items.ISBN] = ISBN
                        it[Items.type] = type
                        it[isBorrowed] = false
                    }
                }
                call.respondText("Item added")
            }

            get("/search") {
                val params = call.request.queryParameters
                val query = params["query"] ?: return@get call.respondText(
                    "Missing search query",
                    status = HttpStatusCode.BadRequest
                )
                val results = transaction {
                    Items.selectAll()
                        .where((Items.title eq query) or (Items.ISBN eq query))
                        .map { it.toItemJson() }
                }
                if (results.isEmpty()) {
                    call.respondText("No items found", status = HttpStatusCode.NotFound)
                } else {
                    call.respondText(Json.encodeToString(ListSerializer(ItemJson.serializer()), results),
                        status = HttpStatusCode.OK)
                }
            }

            post("/borrow") {
                val params = call.receiveParameters()
                val id =
                    params["id"] ?: return@post call.respondText("Missing id", status = HttpStatusCode.BadRequest)
                val username = params["username"] ?: return@post call.respondText(
                    "Missing username",
                    status = HttpStatusCode.BadRequest
                )
                transaction {
                    Items.update({ Items.id eq id.toInt() }) {
                        it[borrower] = username
                        it[borrowedDate] = LocalDate.now().toString()
                        it[dueDate] = LocalDate.now().plusDays(15).toString()
                        it[isBorrowed] = true
                    }
                }
                call.respond(HttpStatusCode.OK)
            }

            post("/return") {
                val params = call.receiveParameters()
                val id =
                    params["id"] ?: return@post call.respondText("Missing id", status = HttpStatusCode.BadRequest)
                transaction {
                    Items.update({ Items.id eq id.toInt() }) {
                        it[borrower] = null
                        it[borrowedDate] = null
                        it[dueDate] = null
                        it[isBorrowed] = false
                    }
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

@Serializable
data class ItemJson(
    val id: Int,
    val title: String,
    val ISBN: String,
    val type: String,
    val borrower: String?,
    val borrowedDate: String?,
    val dueDate: String?,
    val isBorrowed: Boolean
)

fun ResultRow.toItemJson(): ItemJson {
    return ItemJson(
        id = this[Items.id],
        title = this[Items.title],
        ISBN = this[Items.ISBN],
        type = this[Items.type],
        borrower = this[Items.borrower],
        borrowedDate = this[Items.borrowedDate],
        dueDate = this[Items.dueDate],
        isBorrowed = this[Items.isBorrowed]
    )
}