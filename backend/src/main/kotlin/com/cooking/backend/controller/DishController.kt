package com.cooking.backend.controller

import com.cooking.backend.Dto.DishCreateDto
import com.cooking.backend.Dto.DishResponseDto
import com.cooking.backend.service.DishService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import com.cooking.backend.security.UserDetailsImpl

@RestController
@RequestMapping("api/dishes")
class DishController(
    private val dishService: DishService
) {

    @PostMapping
    fun createDish(
        @RequestBody dto: DishCreateDto,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<DishResponseDto> {
        val dish = dishService.createDish(dto, user.username)
        return ResponseEntity.ok(dish)
    }

    @GetMapping("/my")
    fun getUserDishes(
        @AuthenticationPrincipal user: UserDetailsImpl
    ): List<DishResponseDto> {
        return dishService.getUserDishes(user.username)
    }
}