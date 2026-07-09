package com.istts.finalproject.data.remote

import com.istts.finalproject.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // ========== AUTH ==========
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<AuthResponse>

    // ========== MOOD ==========
    @POST("api/mood")
    suspend fun saveMood(
        @Header("Authorization") token: String,
        @Body request: MoodRequest
    ): Response<MoodResponse>

    @GET("api/mood/history")
    suspend fun getMoodHistory(
        @Header("Authorization") token: String,
        @Query("filter") filter: String? = null
    ): Response<MoodHistoryResponse>

    @GET("api/mood/stats")
    suspend fun getMoodStats(
        @Header("Authorization") token: String
    ): Response<MoodStatsResponse>

    // ========== QUOTE ==========
    @GET("api/quote")
    suspend fun getRandomQuote(): Response<QuoteResponse>
}