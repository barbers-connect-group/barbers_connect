import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.barbers_connect.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object UserService {
    private val client = okhttp3.OkHttpClient()

    fun login(context: Context, user: User, callback: (Map<String, String>) -> Unit) {
        val json = JSONObject().apply {
            put("username", user.username)
            put("password", user.password)
        }
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = okhttp3.Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/login ")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Handler(Looper.getMainLooper()).post {
                    callback(mapOf("successMessage" to "", "errorMessage" to "Erro de conexão"))
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Handler(Looper.getMainLooper()).post {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val token = jsonResponse.optString("token", "")

                            if (token.isNotEmpty()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    saveToken(context, token)
                                }

                                callback(mapOf("message" to "Login bem-sucedido", "status" to "success"))
                            } else {
                                callback(mapOf("message" to "Erro ao obter token", "status" to "error"))
                            }
                        }
                        callback(mapOf("message" to "Login bem-sucedido", "status" to "success"))
                    } else {
                        callback(mapOf("message" to "Erro no login", "status" to "error"))
                    }
                }
            }
        })
    }

    fun register(user: User, callback: (Map<String, String>) -> Unit) {
        val json = JSONObject().apply {
            put("username", user.username)
            put("password", user.password)
            put("email", user.email)
        }
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = okhttp3.Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/signup")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Handler(Looper.getMainLooper()).post {
                    callback(mapOf("message" to "Erro de conexão", "status" to "failed"))
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Handler(Looper.getMainLooper()).post {
                    if (response.isSuccessful) {
                        callback(mapOf("message" to "Registro bem-sucedido", "status" to "success"))
                    } else {
                        callback(mapOf("message" to "Erro no Registro", "status" to "error"))
                    }
                }
            }
        })
    }

    fun current_user(context: Context, callback: (Map<String, Any>) -> Unit) {
        val token = getToken(context)
        println("oiii")
        println(token)
        if (token.isNullOrEmpty()) {
            callback(mapOf("message" to "Token não encontrado", "status" to "error"))
            return
        }

        val request = okhttp3.Request.Builder()
            .url("https://barbersconnectapi.vercel.app/api/current_user")
            .addHeader("Authorization", "token $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Handler(Looper.getMainLooper()).post {
                    callback(mapOf("message" to "Erro de conexão", "status" to "failed"))
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Handler(Looper.getMainLooper()).post {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val user = User(
                                id = jsonResponse.optInt("id"),
                                username = jsonResponse.optString("username", ""),
                                email = jsonResponse.optString("email", ""),
                                createdAt = jsonResponse.optString("created_at", "")
                            )
                            callback(
                                mapOf(
                                    "message" to "usuário encontrado com sucesso",
                                    "status" to "success",
                                    "data" to user
                                )
                            )
                        } else {
                            callback(mapOf("message" to "Resposta vazia", "status" to "error"))
                        }
                    } else {
                        callback(mapOf("message" to "Erro ao obter usuário", "status" to "error"))
                    }
                }
            }
        })
    }



    private fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}
