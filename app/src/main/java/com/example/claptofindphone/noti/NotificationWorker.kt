package com.example.claptofindphone.noti

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.claptofindphone.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notiIndex = inputData.getInt("NOTI_INDEX", 0)
        sendNotification(notiIndex)
        return Result.success()
    }

    private fun sendNotification(index: Int) {
        val notificationId = index // Mỗi thông báo có một ID duy nhất
        val customLayoutId = getCustomLayoutId(index)

        val notificationLayout = RemoteViews(applicationContext.packageName, customLayoutId)
        val notificationLayoutExpanded = RemoteViews(applicationContext.packageName, customLayoutId)


        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "daily_notif",
                "Daily Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "daily_notif")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun getCustomLayoutId(index: Int): Int {
        return when (index % 6) {
            0 -> R.layout.notification1
            1 -> R.layout.notification2
            2 -> R.layout.notification3
            3 -> R.layout.notification4
            4 -> R.layout.notification5
            else -> R.layout.notification6
        }
    }
}