package com.example.test_cooking.network

import android.content.Context
import com.example.test_cooking.data.AuthResponse
import com.example.test_cooking.data.LoginData
import com.example.test_cooking.data.RegistrationData
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.test_cooking.data.AuthManager
import com.example.test_cooking.data.User
import com.google.gson.Gson

class AuthService(private val context: Context) {
    sealed class AuthResult {
        data class Success(val authResponse: AuthResponse) : AuthResult()
        data class Error(val message: String) : AuthResult()
        object NetworkError : AuthResult()
    }

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    private val client: OkHttpClient
    private val authManager: AuthManager = AuthManager(context)

    val currentAuthToken: String?
        get() = authManager.getAuthToken()

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    private val BASE_URL = "http://136.0.133.15:8080"

    private val gson = Gson()

    sealed class UserProfileResult {
        data class Success(val user: User) : UserProfileResult()
        data class Error(val message: String) : UserProfileResult()
        data object NetworkError : UserProfileResult()
    }

    suspend fun registerUser(registrationData: RegistrationData): AuthResult {
        val jsonRequest = Json.encodeToString(registrationData)
        val body = jsonRequest.toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$BASE_URL/api/users/register")
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = "Ошибка регистрации"
                        AuthResult.Error(errorBody)
                    } else {
                        val responseBody = response.body?.string()
                        println("Успешный ответ от сервера при регистрации: $responseBody")
                        if (responseBody != null) {
                            try {
                                val authResponse = Json.decodeFromString<AuthResponse>(responseBody)
                                authManager.saveAuthToken(authResponse.token)
                                AuthResult.Success(authResponse)
                            } catch (e: Exception) {
                                AuthResult.Error("Ошибка обработки ответа сервера. Получен неверный формат токена.")
                            }
                        } else {
                            AuthResult.Error("Сервер вернул пустой успешный ответ при регистрации.")
                        }
                    }
                }
            } catch (e: IOException) {
                System.err.println("Ошибка подключения при регистрации: $e")
                AuthResult.NetworkError
            } catch (e: Exception) {
                System.err.println("Непредвиденная ошибка при регистрации: $e")
                AuthResult.Error("Непредвиденная ошибка")
            }
        }
    }

    suspend fun loginUser(loginData: LoginData): AuthResult {
        val jsonRequest = Json.encodeToString(loginData)
        val body = jsonRequest.toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$BASE_URL/api/users/login")
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "Неизвестная ошибка авторизации"
                        AuthResult.Error(errorBody)
                    } else {
                        val responseBody = response.body?.string()
                        println("Успешный ответ от сервера при авторизации: $responseBody")
                        if (responseBody != null) {
                            try {
                                val authResponse = Json.decodeFromString<AuthResponse>(responseBody)
                                authManager.saveAuthToken(authResponse.token)
                                AuthResult.Success(authResponse)
                            } catch (e: Exception) {
                                AuthResult.Error("Ошибка обработки ответа сервера. Получен неверный формат токена.")
                            }
                        } else {
                            AuthResult.Error("Сервер вернул пустой успешный ответ при авторизации.")
                        }
                    }
                }
            } catch (e: IOException) {
                AuthResult.NetworkError
            } catch (e: Exception) {
                AuthResult.Error("Непредвиденная ошибка")
            }
        }
    }

    suspend fun getMyProfile(): UserProfileResult {
        return withContext(Dispatchers.IO) {
            val token = authManager.getAuthToken()
            if (token == null) {
                return@withContext UserProfileResult.Error("Пользователь не авторизован.")
            }

            val request = Request.Builder()
                .url("$BASE_URL/api/users/me") // Замени на свой актуальный URL
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {

                    val user = gson.fromJson(responseBody, User::class.java)
                    UserProfileResult.Success(user)
                } else {
                    UserProfileResult.Error("Ошибка получения профиля")
                }
            } catch (e: IOException) {
                UserProfileResult.NetworkError
            } catch (e: Exception) {
                UserProfileResult.Error("Ошибка обработки данных профиля")
            }
        }
    }
}