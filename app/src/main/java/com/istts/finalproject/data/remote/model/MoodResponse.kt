package com.istts.finalproject.data.remote.model

data class MoodResponse(
    val success: Boolean,
    val message: String,
    val data: MoodData?
)

data class MoodData(
    val _id: String,
    val userId: String,
    val moodLevel: Int,
    val note: String?,
    val date: String  // ISO 8601 format
)

data class MoodHistoryResponse(
    val success: Boolean,
    val data: List<MoodData>
)

data class MoodStatsResponse(
    val success: Boolean,
    val data: MoodStats
)

data class MoodStats(
    val totalEntries: Int,
    val averageMood: Double,
    val highestMood: Int,
    val lowestMood: Int,
    val weeklyAverage: Double,
    val latestMood: MoodData?
)