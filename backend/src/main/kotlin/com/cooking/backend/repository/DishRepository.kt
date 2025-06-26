package com.cooking.backend.repository

import com.cooking.backend.model.Dish
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DishRepository : JpaRepository<Dish, Int> {
    fun findByCreatorLogin(login: String): List<Dish>

    fun findByCategory(category: String): List<Dish>
    @Query("SELECT d FROM Dish d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchByTitle(@Param("query") query: String): List<Dish>
}