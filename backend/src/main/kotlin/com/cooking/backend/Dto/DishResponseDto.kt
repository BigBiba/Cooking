package com.cooking.backend.Dto

data class DishResponseDto(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val recipe: String,
    val category: String,
    val creatorNickname: String,
    val photoUrl: String,
)