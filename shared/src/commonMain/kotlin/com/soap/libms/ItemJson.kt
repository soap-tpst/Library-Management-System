package com.soap.libms

import kotlinx.serialization.Serializable

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
