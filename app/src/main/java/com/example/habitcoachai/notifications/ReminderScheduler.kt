package com.example.habitcoachai.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder

object ReminderScheduler {

    fun scheduleDailyReminder(context: Context) {

        val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS) // 🔥 fires in 10 sec
            .build()

        WorkManager.getInstance(context).enqueue(request)

        Log.d("REMINDER_DEBUG", "One-time reminder scheduled")
    }
}