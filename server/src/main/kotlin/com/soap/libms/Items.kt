package com.soap.libms

import org.jetbrains.exposed.sql.Table

object Items : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 100)
    val ISBN = varchar("ISBN", 50)
    val type = varchar("type", 50)
    val isBorrowed = bool("isBorrowed")
    val borrower = varchar("borrower", 50).nullable()
    val borrowedDate = varchar("borrowedDate", 50).nullable()
    val dueDate = varchar("dueDate", 50).nullable()
    override val primaryKey = PrimaryKey(id)
}