package com.example.test_cooking.data

import kotlinx.serialization.Serializable

@Serializable
data class Dish(
    val title: String,
    val description: String,
    val category: String,
    val ingredients: List<String>,
    val recipe: String,
    val photo: String,
)

@Serializable
data class AnswerDish(
    val title: String,
    val description: String,
    val category: String,
    val ingredients: List<String>,
    val recipe: String,
    val photoUrl: String,
)