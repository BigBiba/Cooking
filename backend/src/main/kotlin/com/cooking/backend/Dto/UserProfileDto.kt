package com.cooking.backend.Dto

import org.hibernate.validator.constraints.URL

data class UserProfileDto(
    val nickname: String?,
    val avatarURL: ByteArray?,
)
