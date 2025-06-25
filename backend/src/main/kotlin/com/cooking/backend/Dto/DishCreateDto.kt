package com.cooking.backend.Dto

data class DishCreateDto (
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val recipe: String,
    val category: String,
    val photoUrl: String
)

