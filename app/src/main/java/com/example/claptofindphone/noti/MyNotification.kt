package com.example.claptofindphone.noti

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.SplashActivity
import com.example.claptofindphone.model.Constant

object MyNotification {
    private var channel: NotificationChannel? = null
    private lateinit var notificationManager: NotificationManager
    fun createNotificationChannel(context: Context) {
        Log.d("Notification", "Notification channel created")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = "Clap to find phone"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            channel = NotificationChannel(
                Constant.Notification.CHANNEL_ID,
                Constant.Notification.CHANNEL_NAME,
                importance
            )
            channel.let { it?.description = description }
            notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel!!)
        }
    }

    @SuppressLint("RemoteViewLayout")
    fun createNotifyOn(context: Context): Notification {
        // Inflate the custom layout using RemoteViews
        val customView_big = RemoteViews(context.packageName, R.layout.big_custom_notify2)
        val customView_small = RemoteViews(context.packageName, R.layout.custom_notifiy2)
        val pendingIntent = getPendingIntent(context, false)

        // Cập nhật customView với PendingIntent mới
        customView_big.setOnClickPendingIntent(R.id.big_custom_notify2_on, pendingIntent)
        customView_small.setOnClickPendingIntent(R.id.custom_notify2_on, pendingIntent)
        customView_big.setOnClickPendingIntent(R.id.power_button_noti, pendingIntent)
        val iconRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            R.drawable.logo2 // icon cho Android 13 trở lên
        } else {
            R.mipmap.ic_launcher// icon cho Android thấp hơn
        }
        // Build and return the notification
        return NotificationCompat.Builder(context, Constant.Notification.CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(iconRes)
            .setCustomContentView(customView_small)
            .setCustomBigContentView(customView_big)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSound(null)
            .setVibrate(null)
            .build()
    }

    @SuppressLint("RemoteViewLayout", "ObsoleteSdkInt")
    fun createNotifyOff(context: Context): Notification {
        // Inflate the custom layout using RemoteViews
        val customView = RemoteViews(context.packageName, R.layout.custom_notify_big)
        val customView2 = RemoteViews(context.packageName, R.layout.custom_notify_small)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            customView.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    context,
                    12,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    context,
                    13,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    context,
                    14,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    context,
                    15,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    context,
                    1435,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            // small notify
            customView2.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    context,
                    12,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    context,
                    13,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    context,
                    14,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    context,
                    15,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    context,
                    1435,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        } else {
            customView.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    context,
                    123,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    context,
                    134,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    context,
                    145,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    context,
                    156,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    context,
                    1435,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            // small notify
            customView2.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    context,
                    123,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    context,
                    134,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    context,
                    145,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    context,
                    156,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    context,
                    1435,
                    Intent(context, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        val iconRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            R.drawable.logo2 // icon cho Android 13 trở lên
        } else {
            R.mipmap.ic_launcher// icon cho Android thấp hơn
        }
        // Build and return the notification
        return NotificationCompat.Builder(context, Constant.Notification.CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customView2).setCustomBigContentView(customView)
            .setContentTitle("Clap to find phone")
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_LOW).setSound(null).setVibrate(null)
            .setOngoing(true).build()
    }

    fun updateOnNotification(context: Context, value: Boolean) {
        val customView_big = RemoteViews(context.packageName, R.layout.big_custom_notify2)
        val customView_small = RemoteViews(context.packageName, R.layout.custom_notifiy2)
        val pendingIntent = getPendingIntent(context, value)
        customView_big.setOnClickPendingIntent(R.id.big_custom_notify2_on, pendingIntent)
        customView_small.setOnClickPendingIntent(R.id.custom_notify2_on, pendingIntent)
        customView_big.setOnClickPendingIntent(R.id.power_button_noti, pendingIntent)
        // Tạo lại thông báo với PendingIntent đã thay đổi
        val notification = NotificationCompat.Builder(context, Constant.Notification.CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.logo2)
            .setCustomContentView(customView_small).setCustomBigContentView(customView_big)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true).setSound(null)
            .setVibrate(null).build()
        notificationManager.notify(1, notification)
    }

    fun updateOffNotification(context: Context) {
        val notification = createNotifyOff(context)
        notificationManager.notify(1, notification)
    }

    private fun getPendingIntent(context: Context, value: Boolean): PendingIntent {

        val pendingIntent = PendingIntent.getActivity(
            context, 5000, Intent(context, SplashActivity::class.java).apply {
                action = value.toString()
            }, PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }
}