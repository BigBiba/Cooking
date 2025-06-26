package com.example.test_cooking.network

import android.content.Context
import android.net.Uri
import com.example.test_cooking.data.AnswerDish
import com.example.test_cooking.data.Dish
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.serialization.json.Json
import okhttp3.Request

class DishService(private val context: Context) {

    sealed class DishesResult {
        data class Success(val dishes: List<AnswerDish>) : DishesResult()
        data class Error(val message: String) : DishesResult()
        object NetworkError : DishesResult()
    }

    private val client: OkHttpClient

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    private val BASE_URL = "http://136.0.133.15:8080"
    private val gson = Gson()

    suspend fun createDish(
        dish: Dish,
        imageUri: Uri,
        authToken: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            var tempFile: File? = null
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: throw IOException("Не удалось открыть InputStream для Uri: $imageUri")

                tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val imageMediaType = context.contentResolver.getType(imageUri)?.toMediaTypeOrNull()
                    ?: "image/jpeg".toMediaTypeOrNull()

                val ingredientsJson = Json.encodeToString(dish.ingredients)

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", dish.title)
                    .addFormDataPart("description", dish.description)
                    .addFormDataPart("category", dish.category)
                    .addFormDataPart("ingredients", ingredientsJson)
                    .addFormDataPart("recipe", dish.recipe)
                    .addFormDataPart("photo", tempFile.name, tempFile.asRequestBody(imageMediaType))
                    .build()

                val request = okhttp3.Request.Builder()
                    .url("$BASE_URL/api/dishes")
                    .header("Authorization", "Bearer $authToken")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        System.err.println("Ошибка при создании блюда: ${response.code} ${response.message} ${authToken.isBlank()}. Тело ответа: ${response.body?.string()}")
                        false
                    } else {
                        println("Блюдо успешно создано: ${response.body?.string()}")
                        true
                    }
                }
            } catch (e: Exception) {
                System.err.println("Ошибка при загрузке блюда: $e")
                false
            } finally {
                tempFile?.delete()
            }
        }
    }


    suspend fun getAllDishes(): DishesResult {
        val request = Request.Builder()
            .url("$BASE_URL/api/dishes/all")
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "Неизвестная ошибка сервера"
                        DishesResult.Error(errorBody)
                    } else {
                        val responseBody = response.body?.string()
                        println("Успешный ответ от сервера при получении блюд: $responseBody")
                        if (responseBody != null && responseBody.isNotBlank()) {
                            try {
                                val dishes = json.decodeFromString<List<AnswerDish>>(responseBody)
                                DishesResult.Success(dishes)
                            } catch (e: Exception) {
                                DishesResult.Error("Ошибка обработки данных от сервера")
                            }
                        } else {
                            DishesResult.Success(emptyList())
                        }
                    }
                }
            } catch (e: IOException) {
                DishesResult.NetworkError
            } catch (e: Exception) {
                DishesResult.Error("Непредвиденная ошибка")
            }
        }
    }

    suspend fun getMyDishes(authToken: String): DishesResult {
        return withContext(Dispatchers.IO) {
            if (authToken.isBlank()) {
                return@withContext DishesResult.Error("Пользователь не авторизован.")
            }

            val request = Request.Builder()
                .url("$BASE_URL/api/dishes/my")
                .header("Authorization", "Bearer $authToken")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val dishListType = object : TypeToken<List<AnswerDish>>() {}.type
                    val dishes: List<AnswerDish> = gson.fromJson(responseBody, dishListType)
                    DishesResult.Success(dishes)
                } else {
                    val errorMessage = responseBody ?: "Неизвестная ошибка сервера"
                    DishesResult.Error("Ошибка получения моих блюд")
                }
            } catch (e: IOException) {
                DishesResult.NetworkError
            } catch (e: Exception) {
                DishesResult.Error("Ошибка обработки данных блюд")
            }
        }
    }
}