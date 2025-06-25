package com.cooking.backend.repository

import com.cooking.backend.model.Dish
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DishRepository : JpaRepository<Dish, Int> {
    fun findByCreatorLogin(login: String): List<Dish>
}