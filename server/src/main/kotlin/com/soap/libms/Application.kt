package com.soap.libms

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
                transaction {
                    Users.insert {
                        it[Users.username] = username
                        it[Users.password] = password
                    }
                }
                call.respondText("User added")
            }

            post("/login") {
                val (username, password) = receiveUsernameAndPassword(call) ?: return@post
                val user = transaction {
                    Users.select((Users.username eq username) and (Users.password eq password))
                        .map { it[Users.username] }
                        .firstOrNull()
                }
                if (user == null) {
                    call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
                } else {
                    val borrowedItems = transaction {
                        Items.select(Items.borrower eq username)
                            .map { it.toUserJson() }
                    }
                    call.respond(HttpStatusCode.OK, mapOf("borrowedItems" to borrowedItems))
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
                    Items.select((Items.title eq query) or (Items.ISBN eq query))
                        .map { it.toItemJson() }
                }
                if (results.isEmpty()) {
                    call.respondText("No items found", status = HttpStatusCode.NotFound)
                } else {
                    call.respond(HttpStatusCode.OK, results)
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

fun ResultRow.toItemJson(): Map<String, Any?> {
    return mapOf(
        "id" to this[Items.id],
        "title" to this[Items.title],
        "ISBN" to this[Items.ISBN],
        "type" to this[Items.type],
        "borrower" to this[Items.borrower],
        "borrowedDate" to this[Items.borrowedDate],
        "dueDate" to this[Items.dueDate],
        "isBorrowed" to this[Items.isBorrowed]
    )
}

fun ResultRow.toUserJson(): Map<String, Any?> {
    return mapOf(
        "username" to this[Users.username],
        "password" to this[Users.password]
    )
}