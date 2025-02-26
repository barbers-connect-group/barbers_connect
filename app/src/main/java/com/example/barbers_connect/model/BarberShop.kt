package com.example.barbers_connect.model


data class BarberShop(
    val name: String,
    val description: String,
    val address: String,
    val phone: String,
    val startShift: String,
    val endShift: String,
    val tags: List<String>
)
