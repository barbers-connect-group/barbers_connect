package com.example.barbers_connect.model

import com.google.firebase.firestore.DocumentId

data class WelcomeMessage(
    @DocumentId
    val id: String = "",

    val weekDay: String = "",
    val message: String = ""
)