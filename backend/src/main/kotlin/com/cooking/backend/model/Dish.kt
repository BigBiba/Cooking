package com.cooking.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime



@Entity
@Table(name = "dishes")
data class Dish(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val title: String,
    val description: String,

    val ingredients: String,

    val recipe: String,
    val category: String,

    val updated_at: LocalDateTime = LocalDateTime.now(),
    val created_at: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "creatorId", referencedColumnName = "id")
    val creator: User,

    val photoUrl: String,

    @OneToMany(mappedBy = "dish", cascade = [CascadeType.ALL])
    val favorites: List<Favorite> = listOf()
)
