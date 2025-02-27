package com.example.claptofindphone.noti

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.SplashActivity
import com.example.claptofindphone.activity.buildMinVersionP
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.utils.SharePreferenceUtils

import java.util.Calendar


object NotificationUtilsNeko2 {


    fun runNotification(context: Context) {

        val indexNoti = SharePreferenceUtils.getIndexNoty()
        val groupLayout = listOf(
            Pair(R.layout.small_notification1, R.layout.notification1),
            Pair(R.layout.small_notification2, R.layout.notification2),
            Pair(R.layout.small_notification3, R.layout.notification3),
            Pair(R.layout.small_notification4, R.layout.notification4),
            Pair(R.layout.small_notification5, R.layout.notification5),
            Pair(R.layout.small_notification6, R.layout.notification6),
            )

        val remoteViews = RemoteViews(context.packageName, groupLayout[indexNoti].first)
        val remoteViewsExpand = RemoteViews(context.packageName, groupLayout[indexNoti].second)

        if (buildMinVersionP()) {
            val channel = NotificationChannel(
                Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager =
                context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val mIntent = Intent(context.applicationContext, SplashActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            (System.currentTimeMillis() / 10000).toInt(),
            mIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, Constant.CHANNEL_ID)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews).setAutoCancel(true).setShowWhen(true)
            .setSmallIcon(R.mipmap.ic_launcher).setWhen(System.currentTimeMillis())
            .setCustomBigContentView(remoteViewsExpand).setContentIntent(pendingIntent)
            .setOngoing(false).priority = NotificationCompat.PRIORITY_HIGH

        val am = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        am.notify(265558, builder.build())
        var newIndex = indexNoti + 1
        if (newIndex == 5) {
            newIndex = 0
        }
        SharePreferenceUtils.setIndexNoty(newIndex)

    }

}