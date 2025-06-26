package com.example.test_cooking.data

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {

    private val PREFS_NAME = "cooking_app_prefs"
    private val KEY_AUTH_TOKEN = "auth_token"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun logout() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }
}