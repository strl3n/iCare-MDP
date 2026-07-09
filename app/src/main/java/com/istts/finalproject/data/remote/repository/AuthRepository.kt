package com.istts.finalproject.data.remote.repository

import com.istts.finalproject.data.local.dao.UserDao
import com.istts.finalproject.data.local.entity.UserEntity
import com.istts.finalproject.data.remote.ApiService
import com.istts.finalproject.data.remote.model.LoginRequest
import com.istts.finalproject.data.remote.model.RegisterRequest
import com.istts.finalproject.data.remote.model.GoogleLoginRequest
import com.istts.finalproject.utils.Resource
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    // Simpan/replace user lokal dengan aman:
    // - Kalau email sudah ada di Room, REUSE id lokal yang sama (supaya history mood tetap nyambung antar sesi login)
    // - Kalau belum ada, insert baru dan pakai id hasil auto-generate Room
    // Ini memperbaiki bug: id selalu 0 sebelumnya, sehingga semua akun berbagi userId yang sama.
    private suspend fun upsertLocalUser(
        name: String,
        email: String,
        profilePicture: String?,
        isGoogleLogin: Boolean
    ): UserEntity {
        val existing = userDao.getUserByEmail(email)
        val localId = existing?.id ?: 0

        val userToSave = UserEntity(
            id = localId,
            name = name,
            email = email,
            profilePicture = profilePicture,
            isGoogleLogin = isGoogleLogin
        )

        val generatedId = userDao.insertUser(userToSave)
        val finalId = if (localId != 0) localId else generatedId.toInt()

        return userToSave.copy(id = finalId)
    }

    suspend fun register(name: String, email: String, password: String): Resource<UserEntity> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val userData = response.body()?.data
                if (userData != null) {
                    val user = upsertLocalUser(
                        name = userData.name,
                        email = userData.email,
                        profilePicture = userData.profilePicture,
                        isGoogleLogin = userData.isGoogleLogin
                    )
                    Resource.Success(user)
                } else {
                    Resource.Error("Data user tidak ditemukan")
                }
            } else {
                Resource.Error(response.body()?.message ?: "Registrasi gagal")
            }
        } catch (e: IOException) {
            Resource.Error("Error jaringan: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): Resource<Pair<UserEntity, String>> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val userData = response.body()?.data
                val token = response.body()?.token
                if (userData != null && token != null) {
                    val user = upsertLocalUser(
                        name = userData.name,
                        email = userData.email,
                        profilePicture = userData.profilePicture,
                        isGoogleLogin = userData.isGoogleLogin
                    )
                    Resource.Success(Pair(user, token))
                } else {
                    Resource.Error("Data user atau token tidak ditemukan")
                }
            } else {
                Resource.Error(response.body()?.message ?: "Login gagal")
            }
        } catch (e: IOException) {
            Resource.Error("Error jaringan: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    suspend fun googleLogin(email: String, name: String, profilePicture: String?): Resource<Pair<UserEntity, String>> {
        return try {
            val response = apiService.googleLogin(GoogleLoginRequest(email, name, profilePicture))
            if (response.isSuccessful && response.body()?.success == true) {
                val userData = response.body()?.data
                val token = response.body()?.token
                if (userData != null && token != null) {
                    val user = upsertLocalUser(
                        name = userData.name,
                        email = userData.email,
                        profilePicture = userData.profilePicture,
                        isGoogleLogin = true
                    )
                    Resource.Success(Pair(user, token))
                } else {
                    Resource.Error("Data user atau token tidak ditemukan")
                }
            } else {
                Resource.Error(response.body()?.message ?: "Google login gagal")
            }
        } catch (e: IOException) {
            Resource.Error("Error jaringan: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    suspend fun getCurrentUser(userId: Int): Resource<UserEntity> {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Error("User tidak ditemukan")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }
}
