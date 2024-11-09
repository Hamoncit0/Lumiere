package com.example.lumiere.Models

data class User(
    var id: Int? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var username: String? = null,
    var email: String? = null,
    var password: String? = null,
    var status: Int?= null, // 0 para inactivo, 1 para activo  // true para activo, false para inactivo
    var profile_picture: String? = null,
    var createdAt: String? = null
)
