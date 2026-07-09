package com.istts.finalproject.data.remote.model

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?,
    val token: String?
)

data class UserData(
    val id: String,  // ✅ Sudah String, sesuai dengan MongoDB _id
    val name: String,
    val email: String,
    val profilePicture: String?,
    val isGoogleLogin: Boolean
)