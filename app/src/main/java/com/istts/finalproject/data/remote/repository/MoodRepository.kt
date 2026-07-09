package com.istts.finalproject.data.remote.repository

import com.istts.finalproject.data.local.dao.MoodDao
import com.istts.finalproject.data.local.entity.MoodEntity
import com.istts.finalproject.data.remote.ApiService
import com.istts.finalproject.data.remote.model.MoodRequest
import com.istts.finalproject.utils.Resource
import java.text.SimpleDateFormat
import java.util.*

class MoodRepository(
    private val apiService: ApiService,
    private val moodDao: MoodDao
) {

    suspend fun saveMood(token: String, userId: Int, moodLevel: Int, note: String?): Resource<MoodEntity> {
        return try {
            val response = apiService.saveMood(
                "Bearer $token",
                MoodRequest(moodLevel, note)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val moodData = response.body()?.data
                if (moodData != null) {
                    val date = try {
                        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                        format.parse(moodData.date) ?: Date()
                    } catch (e: Exception) {
                        Date()
                    }

                    val mood = MoodEntity(
                        userId = userId,
                        moodLevel = moodData.moodLevel,
                        note = moodData.note,
                        date = date
                    )
                    moodDao.insertMood(mood)
                    Resource.Success(mood)
                } else {
                    Resource.Error("Data mood tidak ditemukan")
                }
            } else {
                val mood = MoodEntity(
                    userId = userId,
                    moodLevel = moodLevel,
                    note = note,
                    date = Date()
                )
                moodDao.insertMood(mood)
                Resource.Success(mood)
            }
        } catch (e: Exception) {
            val mood = MoodEntity(
                userId = userId,
                moodLevel = moodLevel,
                note = note,
                date = Date()
            )
            moodDao.insertMood(mood)
            Resource.Success(mood)
        }
    }

    suspend fun getMoodHistory(userId: Int): Resource<List<MoodEntity>> {
        return try {
            val moods = moodDao.getMoodsByUser(userId)
            Resource.Success(moods)
        } catch (e: Exception) {
            Resource.Error("Gagal mengambil history: ${e.message}")
        }
    }

    suspend fun getLatestMood(userId: Int): Resource<MoodEntity?> {
        return try {
            val mood = moodDao.getLatestMood(userId)
            Resource.Success(mood)
        } catch (e: Exception) {
            Resource.Error("Gagal mengambil mood terbaru: ${e.message}")
        }
    }

    suspend fun getWeeklyAverage(userId: Int): Resource<Double?> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startDate = calendar.time
            val average = moodDao.getAverageMood(userId, startDate)
            Resource.Success(average)
        } catch (e: Exception) {
            Resource.Error("Gagal menghitung rata-rata: ${e.message}")
        }
    }

    suspend fun getMoodStats(userId: Int): Resource<MoodStats> {
        return try {
            val moods = moodDao.getMoodsByUser(userId)
            val latestMood = moods.firstOrNull()
            val averageMood = if (moods.isNotEmpty()) {
                moods.map { it.moodLevel }.average()
            } else 0.0
            val highestMood = moods.maxByOrNull { it.moodLevel }?.moodLevel ?: 0
            val lowestMood = moods.minByOrNull { it.moodLevel }?.moodLevel ?: 0
            val totalEntries = moods.size

            val stats = MoodStats(
                latestMood = latestMood,
                averageMood = averageMood,
                highestMood = highestMood,
                lowestMood = lowestMood,
                totalEntries = totalEntries
            )
            Resource.Success(stats)
        } catch (e: Exception) {
            Resource.Error("Gagal mengambil statistik: ${e.message}")
        }
    }

    suspend fun deleteMood(moodId: Int): Resource<Unit> {
        return try {
            moodDao.deleteMood(moodId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus mood: ${e.message}")
        }
    }

    suspend fun clearUserMoods(userId: Int): Resource<Unit> {
        return try {
            moodDao.deleteMoodsByUser(userId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal membersihkan data mood: ${e.message}")
        }
    }
}

data class MoodStats(
    val latestMood: MoodEntity?,
    val averageMood: Double,
    val highestMood: Int,
    val lowestMood: Int,
    val totalEntries: Int
)