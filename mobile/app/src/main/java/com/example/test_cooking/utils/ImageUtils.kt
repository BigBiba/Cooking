package com.example.test_cooking.utils

import android.util.Base64

fun decodeBase64ToByteArray(base64Str: String?): ByteArray? {
    if (base64Str == null) {
        return null
    }

    val pureBase64 = if (base64Str.startsWith("data:image/", ignoreCase = true)) {
        base64Str.substringAfterLast(",", "")
    } else {
        base64Str
    }

    return try {
        Base64.decode(pureBase64, Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: Exception) {
        null
    }
}