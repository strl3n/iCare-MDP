package com.istts.finalproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.istts.finalproject.data.local.entity.MoodEntity
import java.util.Date

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMood(mood: MoodEntity): Long

    @Query("SELECT * FROM moods WHERE userId = :userId ORDER BY date DESC")
    suspend fun getMoodsByUser(userId: Int): List<MoodEntity>

    @Query("SELECT * FROM moods WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getMoodsByDateRange(userId: Int, startDate: Date, endDate: Date): List<MoodEntity>

    @Query("SELECT * FROM moods WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMood(userId: Int): MoodEntity?

    @Query("SELECT AVG(moodLevel) FROM moods WHERE userId = :userId AND date >= :startDate")
    suspend fun getAverageMood(userId: Int, startDate: Date): Double?

    @Query("DELETE FROM moods WHERE id = :moodId")
    suspend fun deleteMood(moodId: Int)

    @Query("DELETE FROM moods WHERE userId = :userId")
    suspend fun deleteMoodsByUser(userId: Int)

    @Query("SELECT COUNT(*) FROM moods WHERE userId = :userId")
    suspend fun getMoodCount(userId: Int): Int
}