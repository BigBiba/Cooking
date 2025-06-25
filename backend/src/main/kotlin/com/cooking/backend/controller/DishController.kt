package com.cooking.backend.controller

import com.cooking.backend.Dto.DishCreateDto
import com.cooking.backend.Dto.DishResponseDto
import com.cooking.backend.service.DishService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import com.cooking.backend.security.UserDetailsImpl
import org.springframework.security.core.userdetails.UsernameNotFoundException

@RestController
@RequestMapping("api/dishes")
class DishController(
    private val dishService: DishService
) {

    @PostMapping
    fun createDish(
        @RequestBody dto: DishCreateDto,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<Any> {
        return try {
            val dish = dishService.createDish(dto, user.username)
            return ResponseEntity.ok(dish)
        } catch (e: UsernameNotFoundException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/my")
    fun getUserDishes(
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(dishService.getUserDishes(user.username))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/all")
    fun getAllDishes(
        @RequestParam(required = false) category: String?
    ): List<DishResponseDto> {
        return if (category != null) {
            dishService.getDishesByCategory(category)
        } else {
            dishService.getAllDishes()
        }
    }

    @GetMapping("/search")
    fun searchDishes(
        @RequestParam q: String,
    ): ResponseEntity<List<DishResponseDto>> {
        return ResponseEntity.ok(
            dishService.searchDishes(q)
        )
    }
}