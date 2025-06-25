package com.cooking.backend.Dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


data class UserRegisterDto(
    val nickname: String,

    @field:NotBlank(message = "Login is required")
    @field:Size(min = 3, max = 20, message = "Username must be 3-20 characters long")
    val login: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String,
)
