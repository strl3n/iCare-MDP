package com.istts.finalproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val moodLevel: Int,
    val note: String? = null,
    val date: Date = Date()
)