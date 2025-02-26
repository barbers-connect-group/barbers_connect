package com.example.barbers_connect.model

data class User(
    val id: Int? = null,
    val username: String,
    val password: String? = null,
    val email: String? = null,
    val createdAt: String? = null
)