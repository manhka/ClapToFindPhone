package com.example.claptofindphone.noti

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler {
    fun scheduleDailyNotifications(context: Context) {
        for (i in 0..5) {
            // 6 thông báo
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setInputData(workDataOf("NOTI_INDEX" to i))
                .setInitialDelay(calculateInitialDelay(19, 0, i), TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "Noti_Work_$i",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun calculateInitialDelay(hour: Int, minute: Int, index: Int): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute + (index * 10)) // Thêm độ trễ giữa các thông báo
            set(Calendar.SECOND, 0)
        }

        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = targetTime.timeInMillis - currentTime.timeInMillis
        return delay
    }
}