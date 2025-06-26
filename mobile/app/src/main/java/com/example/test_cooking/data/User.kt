package com.example.test_cooking.data

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationData(
    val nickname: String,
    val login: String,
    val password: String,
)

@Serializable
data class LoginData(
    val login: String,
    val password: String
)

data class User(
    val nickname: String,
    val avatarUrl: String?,
)

