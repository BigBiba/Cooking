package com.cooking.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "dishphotos")
data class DishPhoto(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val photoUrl: String,

    @ManyToOne
    @JoinColumn(name = "dishId", referencedColumnName = "id")
    val dish: Dish
)
