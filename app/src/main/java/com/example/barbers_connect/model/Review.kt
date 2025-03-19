package com.example.barbers_connect.model;


data class Review(
        val id: Int? = null,
        val barbershopId: Int,
        val description: String,
        val rating: Int,
        val imagePath: String? = null,
        val createdAt: Long = System.currentTimeMillis()
)