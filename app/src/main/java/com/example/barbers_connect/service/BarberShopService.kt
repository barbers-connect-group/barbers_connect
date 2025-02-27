package com.example.barbers_connect.service

import UserService
import android.content.Context
import android.util.Log
import com.example.barbers_connect.model.BarberShop
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object BarberShopService {
    private val client = OkHttpClient()

    // Function to get the barbershop profile from the server
    fun getBarberShopProfile(context: Context, barberShopId: Int, callback: (BarberShop?, String) -> Unit) {
        val token = UserService.getToken(context)
        val request = Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/barbershops/$barberShopId")
            .header("Authorization", "Token $token")
            .build()

        Log.d("BarberShopService", "Chamando API para buscar barbearia ID: $barberShopId")

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // If there's an error in the network request, call the callback with an error message
                Log.e("BarberShopService", "Erro na requisição: ${e.message}")
                callback(null, "Erro de conexão")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    // Parse the response body (assuming JSON response)
                    val responseBody = response.body?.string()
                    Log.d("BarberShopService", "Resposta recebida: $responseBody")
                    if (responseBody != null) {
                        try {
                            val json = JSONObject(responseBody)
                            // Map the response to a BarberShop object
                            val barbershop = BarberShop(
                                name = json.getString("name"),
                                description = json.getString("description"),
                                address = json.getString("address"),
                                phone = json.getString("phone"),
                                startShift = json.optString("start_shift", "00:00:00"), // Usa um valor padrão se não existir
                                endShift = json.optString("end_shift", "00:00:00"),
                                tags = json.getJSONArray("tags").let { array ->
                                    mutableListOf<String>().apply {
                                        for (i in 0 until array.length()) {
                                            add(array.getString(i))
                                        }
                                    }
                                }
                            )
                            callback(barbershop, "") // Pass the profile to the callback
                        } catch (e: Exception) {
                            Log.e("BarberShopService", "Erro ao processar JSON: ${e.message}")
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
