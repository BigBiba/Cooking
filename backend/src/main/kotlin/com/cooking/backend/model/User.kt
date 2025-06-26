package com.cooking.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User @JvmOverloads constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val nickname: String = "",
    val login: String = "",
    val password: String = "",

    @Lob
    @Basic(fetch = FetchType.LAZY)
    val avatarUrl: ByteArray? = null,

    val updated_at: LocalDateTime = LocalDateTime.now(),
    val created_at: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val favorites: List<Favorite> = listOf()
)
