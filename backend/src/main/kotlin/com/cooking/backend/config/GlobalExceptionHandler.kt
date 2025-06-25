package com.cooking.backend.config


import org.springframework.http.*
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.fieldErrors.forEach {
            errors[it.field] = it.defaultMessage ?: "Invalid value"
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }
}
