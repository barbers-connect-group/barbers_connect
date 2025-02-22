import com.example.barbers_connect.model.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

object UserService {
    private val client = okhttp3.OkHttpClient()

    fun login(user: User, callback: (Map<String, String>) -> Unit) {
        val json = org.json.JSONObject().apply {
            put("username", user.username)
            put("password", user.password)
        }
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = okhttp3.Request.Builder()
            .url("https://django-deployment-ten.vercel.app/api/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                callback(mapOf("successMessage" to "", "errorMessage" to "Erro de conexão"))
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    callback(mapOf("successMessage" to "Login bem-sucedido", "errorMessage" to ""))
                } else {
                    callback(mapOf("successMessage" to "", "errorMessage" to "Erro no login"))
                }
            }
        })
    }
}
