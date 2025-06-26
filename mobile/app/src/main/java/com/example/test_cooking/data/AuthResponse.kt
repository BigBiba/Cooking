package com.example.test_cooking.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
)