package com.example.habitcoachai.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.habitcoachai.R

class HabitReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {


    override fun doWork(): Result {
        Log.d("REMINDER_DEBUG", "Worker executed")
        showNotification()
        return Result.success()
    }

    private fun showNotification() {

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        val channelId = "habit_reminder_channel_v2"

        // ✅ Always create/update channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH // ⭐ MUST be HIGH
            ).apply {
                description = "Daily habit reminder"
                enableVibration(true)
            }

            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle("HabitCoachAI")
                .setContentText("Don't forget to complete your habits today!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            manager.notify(1001, notification)
        }
    }
}