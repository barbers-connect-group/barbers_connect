package com.example.barbers_connect.service

import com.example.barbers_connect.model.BarberShop
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object BarberShopService {
    private val client = OkHttpClient()

    // Function to get the barbershop profile from the server
    fun getBarberShopProfile(callback: (BarberShop?, String) -> Unit) {
        // Create the request to fetch the barbershop profile (GET request)
        val request = Request.Builder()
            .url("https://django-deployment-ten.vercel.app/api/barbershop/150") // Change this to your actual API endpoint
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // If there's an error in the network request, call the callback with an error message
                callback(null, "Erro de conexão")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    // Parse the response body (assuming JSON response)
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val json = JSONObject(responseBody)
                            // Map the response to a BarberShop object
                            val barbershop = BarberShop(
                                name = json.getString("name"),
                                description = json.getString("description"),
                                address = json.getString("address"),
                                phone = json.getString("phone"),
                                startShift = json.getString("startShift"),
                                endShift = json.getString("endShift"),
                                tags = json.getJSONArray("haircuts").let { array ->
                                    mutableListOf<String>().apply {
                                        for (i in 0 until array.length()) {
                                            add(array.getString(i))
                                        }
                                    }
                                }
                            )
                            callback(barbershop, "") // Pass the profile to the callback
                        } catch (e: Exception) {
                            callback(null, "Erro ao processar os dados")
                        }
                    } else {
                        callback(null, "Erro ao processar os dados")
                    }
                } else {
                    // If the response is not successful
                    callback(null, "Erro ao buscar o perfil")
                }
            }
        })
    }
}
