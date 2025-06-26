package com.cooking.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "favorites")
data class Favorite(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    val user: User,

    @ManyToOne
    @JoinColumn(name = "dishId", referencedColumnName = "id")
    val dish: Dish
)
