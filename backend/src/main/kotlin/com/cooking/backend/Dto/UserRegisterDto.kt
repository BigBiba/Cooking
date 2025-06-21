package com.cooking.backend.Dto

data class UserRegisterDto(
    val nickname: String,
    val login: String,
    val password: String,
    val avatarUrl: String? = null
)
