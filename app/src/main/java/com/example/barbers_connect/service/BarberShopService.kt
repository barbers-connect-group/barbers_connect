package com.example.barbers_connect.service

import UserService
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.barbers_connect.model.BarberShop
import com.example.barbers_connect.model.Review
import com.example.barbers_connect.model.Tag
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object BarberShopService {
    private val client = OkHttpClient()

    fun getAllBarberShops(context: Context, callback: (Map<String, Any>) -> Unit) {
        val token = UserService.getToken(context)
        val request = Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/barbershops")
            .get()
            .header("Authorization", "Token $token")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                callback(mapOf("message" to "Erro: ${e.message}"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val json = JSONObject(responseBody)
                            val barberShopList = mutableListOf<BarberShop>()
                            val results = json.getJSONArray("results")

                            for (i in 0 until results.length()) {
                                    val barbershopJson = results.getJSONObject(i)
                                    val tagsList = mutableListOf<Tag>()
                                    val tagsArray = barbershopJson.getJSONArray("tags")

                                    for (j in 0 until tagsArray.length()) {

                                            val tagObject = tagsArray.getJSONObject(j)
                                            tagsList.add(
                                                Tag(
                                                    id = tagObject.getInt("id"),
                                                    name = tagObject.getString("name")
                                                )
                                            )
                                    }

                                    val barbershop = BarberShop(
                                        id = barbershopJson.getString("id").toInt(),
                                        name = barbershopJson.getString("name"),
                                        description = barbershopJson.getString("description"),
                                        address = barbershopJson.getString("address"),
                                        phone = barbershopJson.getString("phone"),
                                        startShift = barbershopJson.getString("start_shift"),
                                        endShift = barbershopJson.getString("end_shift"),
                                        tags = tagsList
                                    )
                                    barberShopList.add(barbershop)
                            }
                            callback(
                                mapOf(
                                    "message" to "Lista de barbearias encontradas com sucesso",
                                    "status" to "success",
                                    "data" to barberShopList
                                )
                            )
                        }
                        catch (e: Exception) {
                            callback(mapOf("message" to "Erro: ${e.message}"))
                        }
                    } else {
                        callback(mapOf("message" to "Resposta vazia"))
                    }
                } else {
                    callback(mapOf("message" to "Erro: ${response.code}: ${response.message}"))
                }
            }
        })
    }

    // Function to get the barbershop profile from the server
    fun getBarberShopProfile(context: Context, barberShopId: Int, callback: (BarberShop?, String) -> Unit) {
        val token = UserService.getToken(context)
        val request = Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/barbershops/$barberShopId")
            .get()
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
                                id = json.getString("id").toInt(),
                                name = json.getString("name"),
                                description = json.getString("description"),
                                address = json.getString("address"),
                                phone = json.getString("phone"),
                                startShift = json.optString("start_shift", "00:00:00"), // Usa um valor padrão se não existir
                                endShift = json.optString("end_shift", "00:00:00"),
                                tags = json.getJSONArray("tags").let { array ->
                                    mutableListOf<Tag>().apply {
                                        for (i in 0 until array.length()) {
                                            val tagObject = array.getJSONObject(i)
                                            add(Tag(id = tagObject.getInt("id"), name = tagObject.getString("name")))
                                        }
                                    }
                                }
                            )
                            Log.d("barberID", barbershop.toString())
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

    fun getBarberShopReviews(context: Context, barberShopId: Int, callback: (Map<String, Any>) -> Unit) {
        val token = UserService.getToken(context)
        val request = Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/reviews/?barbershop_id=$barberShopId")
            .get()
            .header("Authorization", "Token $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(mapOf("message" to "Erro: ${e.message}"))
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val json = JSONObject(responseBody)
                            val reviewsList = mutableListOf<Review>()
                            val results = json.getJSONArray("results")
                            Log.e("results:", results.toString())
                            for (i in 0 until results.length()) {
                                val reviewJson = results.getJSONObject(i)
                                val review = Review(
                                    id = reviewJson.getString("id").toInt(),
                                    description = reviewJson.getString("description"),
                                    rating = reviewJson.getString("rating").toInt(),
                                    imagePath = reviewJson.getString("image_path"),
                                    createdAt = reviewJson.optString("created_at", "")
                                        .takeIf { it.isNotEmpty() }?.let {
                                            val formatter = DateTimeFormatter.ISO_DATE_TIME
                                            val localDateTime = LocalDateTime.parse(it, formatter)
                                            localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
                                        } ?: System.currentTimeMillis(),
                                    barbershopId = reviewJson.getString("barbershop").toInt(),
                                )
                                reviewsList.add(review)
                            }
                            callback(
                                mapOf(
                                    "message" to "Lista de reviews encontradas com sucesso",
                                    "status" to "success",
                                    "data" to reviewsList
                                )
                            )
                        }
                        catch (e: Exception) {
                            callback(mapOf("message" to "Erro: ${e.message}"))
                        }
                    } else {
                        callback(mapOf("message" to "Resposta vazia"))
                    }
                } else {
                    callback(mapOf("message" to "Erro: ${response.code}: ${response.message}"))
                }
            }
        })
    }
}
