package com.istts.finalproject.data.remote.model

data class MoodRequest(
    val moodLevel: Int,
    val note: String? = null,
    val date: String? = null
)