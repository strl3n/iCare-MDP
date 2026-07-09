package com.istts.finalproject.data.remote.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    val email: String,
    val name: String,
    val profilePicture: String? = null
)