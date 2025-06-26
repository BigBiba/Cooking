package com.cooking.backend.controller

import com.cooking.backend.Dto.UserLoginDto
import com.cooking.backend.Dto.UserRegisterDto
import com.cooking.backend.security.JwtUtil
import com.cooking.backend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails

@RestController
@RequestMapping("api/users")
@Validated
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid request: UserRegisterDto): ResponseEntity<Any> {
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

    @GetMapping("/me")
    fun getMyProfile(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Any> {
        val profile = userService.getUserProfileByUsername(userDetails.username)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/{id}")
    fun getUserProfile(@PathVariable id: Long): ResponseEntity<Any> {
        val userProfile = userService.getUserProfileById(id)
        return if (userProfile != null)
            ResponseEntity.ok(userProfile)
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")
    }

}