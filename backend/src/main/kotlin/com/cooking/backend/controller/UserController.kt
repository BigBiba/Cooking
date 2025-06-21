package com.cooking.backend.controller

import com.cooking.backend.Dto.UserLoginDto
import com.cooking.backend.Dto.UserRegisterDto
import com.cooking.backend.security.JwtUtil
import com.cooking.backend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegisterDto): ResponseEntity<Any> {
        return try {
            val user = userService.registerUser(request)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginDto): ResponseEntity<String> {
        val token = userService.loginUser(request)
        return ResponseEntity.ok(token)
    }

}