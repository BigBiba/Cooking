package com.cooking.backend.service

import com.cooking.backend.Dto.UserLoginDto
import com.cooking.backend.Dto.UserRegisterDto
import com.cooking.backend.model.User
import com.cooking.backend.repository.UserRepository
import com.cooking.backend.security.JwtUtil
import com.cooking.backend.security.UserDetailsImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByLogin(username)
            ?: throw UsernameNotFoundException("User not found with login: $username")
        return UserDetailsImpl(user)
    }


    fun registerUser(request: UserRegisterDto) {
        if (userRepository.findByLogin(request.login) != null) {
            throw IllegalArgumentException("Username already exists")
        }

        val user = User(
            nickname = request.nickname,
            login = request.login,
            password = passwordEncoder.encode(request.password),
            created_at = LocalDateTime.now(),
            updated_at = LocalDateTime.now()
        )

        userRepository.save(user)
    }

    fun loginUser(request: UserLoginDto): String {
        val authToken = UsernamePasswordAuthenticationToken(request.login, request.password)
        val authentication = authenticationManager.authenticate(authToken)
        SecurityContextHolder.getContext().authentication = authentication

        // Генерируем JWT токен по успешной аутентификации
        return jwtUtil.generateToken(request.login)
    }


}