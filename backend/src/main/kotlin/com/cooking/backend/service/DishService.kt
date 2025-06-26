package com.cooking.backend.service

import com.cooking.backend.Dto.DishCreateDto
import com.cooking.backend.Dto.DishResponseDto
import com.cooking.backend.model.Dish
import com.cooking.backend.repository.DishRepository
import com.cooking.backend.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class DishService(
    private val dishRepository: DishRepository,
    private val userRepository: UserRepository
) {

    fun createDish(dto: DishCreateDto, username: String): DishResponseDto {
        val creator = userRepository.findByLogin(username)
            ?: throw UsernameNotFoundException("User not found with login: $username")


        val dish = Dish(
            title = dto.title,
            description = dto.description,
            ingredients = dto.ingredients.joinToString("\n"),
            recipe = dto.recipe,
            creator = creator,
            category = dto.category,
            photoUrl = dto.photoUrl
        )

        val savedDish = dishRepository.save(dish)
        return mapToDto(savedDish)
    }

    fun getUserDishes(username: String ): List<DishResponseDto>
    {
        val user = userRepository.findByLogin(username)
            ?: throw IllegalArgumentException("User not found")

        return dishRepository.findByCreatorLogin(user.login).map{dish -> mapToDto(dish)}
    }

    fun getAllDishes(): List<DishResponseDto> {
        return dishRepository.findAll()
            .map { dish -> mapToDto(dish) }
    }

    fun getDishesByCategory(category: String): List<DishResponseDto> {
        return dishRepository.findByCategory(category)
            .map { dish -> mapToDto(dish) }
    }

    fun searchDishes(query: String): List<DishResponseDto> {
        return dishRepository.searchByTitle(query)
            .map { dish -> mapToDto(dish) }
    }

    private fun mapToDto(dish: Dish): DishResponseDto {
        return DishResponseDto(
            title = dish.title,
            description = dish.description,
            creatorNickname = dish.creator.nickname,
            ingredients =  dish.ingredients.split("\n").map { it.trim() }.filter { it.isNotEmpty() },
            recipe = dish.recipe,
            photoUrl = dish.photoUrl,
            category = dish.category
        )

    }

}