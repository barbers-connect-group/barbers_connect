package com.example.barbers_connect.service

import com.example.barbers_connect.model.WelcomeMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.*

class WelcomeMesageService {

    private val db = FirebaseFirestore.getInstance()

    fun search(callback: (WelcomeMessage?) -> Unit) {
        val weekDay = getCurrentWeekDay()
        println(weekDay)
        db.collection("welcomemessages").whereEqualTo("weekDay", weekDay).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val welcomeMessage = document.documents[0].toObject<WelcomeMessage>()
                    callback(welcomeMessage)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    private fun getCurrentWeekDay(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }
}
