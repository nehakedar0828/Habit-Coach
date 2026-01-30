package com.example.habitcoachai.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.habitcoachai.data.local.dao.HabitCompletionDao
import com.example.habitcoachai.data.local.dao.HabitDao
import com.example.habitcoachai.data.local.entity.HabitEntity
import com.example.habitcoachai.data.local.entity.HabitCompletionEntity


@Database(
    entities = [HabitEntity::class,
               HabitCompletionEntity::class],
    version = 3,
    exportSchema = false
)

abstract class HabitDatabase : RoomDatabase(){

    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao


    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}