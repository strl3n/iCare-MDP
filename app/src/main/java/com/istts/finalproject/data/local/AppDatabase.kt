package com.istts.finalproject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.istts.finalproject.data.local.dao.MoodDao
import com.istts.finalproject.data.local.dao.UserDao
import com.istts.finalproject.data.local.entity.MoodEntity
import com.istts.finalproject.data.local.entity.UserEntity
import com.istts.finalproject.utils.DateConverter

@Database(
    entities = [UserEntity::class, MoodEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "icare_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}