package com.istts.finalproject.utils

import android.content.Context
import android.content.SharedPreferences
import com.istts.finalproject.data.local.entity.UserEntity

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("iCareSession", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_IS_GOOGLE_LOGIN = "is_google_login"
        const val KEY_TOKEN = "token"
    }

    fun saveUserSession(user: UserEntity, token: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_GOOGLE_LOGIN, user.isGoogleLogin)
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isGoogleLogin(): Boolean = prefs.getBoolean(KEY_IS_GOOGLE_LOGIN, false)

    fun getSharedPreferences(): SharedPreferences = prefs

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}