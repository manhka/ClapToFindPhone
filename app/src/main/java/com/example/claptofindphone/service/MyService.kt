package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioRecord
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.core.app.NotificationCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.FoundPhoneActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class MyService : Service() {
    private var backInvokedCallback: OnBackInvokedCallback? = null
    private lateinit var notificationManager: NotificationManager
    private var handlerClap: Handler? = null
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var channel: NotificationChannel? = null

    companion object {
        private const val CHANNEL_ID = "clap_service_channel"
        private const val CHANNEL_NAME = "Clap Detection Service"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        handlerClap = Handler()
    }

    fun handleBackPress(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            backInvokedCallback = OnBackInvokedCallback {
            }
            activity.onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                backInvokedCallback!!
            )
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotifyOn()
        startForeground(1, notification)
        startListening()
        return START_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = "Clap to find phone"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.let { it?.description = description }
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel!!)
        }
    }


    @SuppressLint("RemoteViewLayout")
    private fun createNotifyOff(): Notification {
        // Inflate the custom layout using RemoteViews
        val customView = RemoteViews(packageName, R.layout.custom_notify)

        // Build and return the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView)
            .setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)
            .setVibrate(longArrayOf(0))
            .setOngoing(true)
            .build()
    }
    @SuppressLint("RemoteViewLayout")
    private fun createNotifyOn(): Notification {
        // Inflate the custom layout using RemoteViews
        val customView = RemoteViews(packageName, R.layout.big_custom_notify2)
        val customView2 = RemoteViews(packageName, R.layout.custom_notifiy2)

        // Build and return the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Clap to find phone is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView2)
            .setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSound(null)
            .setVibrate(longArrayOf(0))
            .build()
    }


    private fun startListening() {
        // If the audio classifier is initialized and running, do nothing.
        if (audioClassifier != null) return
        // Initialize the audio classifier
        val classifier = AudioClassifier.createFromFile(this, "voice_detect.tflite")
        val audioTensor = classifier.createInputTensorAudio()
        // Initialize the audio recorder
        val record = classifier.createAudioRecord()
        record.startRecording()
        // Define the classification runnable
        val run = object : Runnable {
            override fun run() {
                // Load the latest audio sample
                audioTensor.load(record)
                val output = classifier.classify(audioTensor)
                // Filter out results above a certain threshold, and sort them descendingly
                val filteredModelOutput = output[0].categories.filter {
                    it.score > 0.3f
                }.sortedBy {
                    -it.score
                }

                if (filteredModelOutput.isNotEmpty()) {
                    Log.e(
                        "Dectecteddddd",
                        filteredModelOutput[0].label.toString() + " , " + filteredModelOutput[0].index.toString()
                    )
                    if (filteredModelOutput[0].index == 56 || filteredModelOutput[0].index == 57 || filteredModelOutput[0].index == 58 || filteredModelOutput[0].index == 35) {
                        foundPhone()
                    } else if (filteredModelOutput[0].index == 426 || filteredModelOutput[0].index == 479 || filteredModelOutput[0].index == 396 || filteredModelOutput[0].index == 79 || filteredModelOutput[0].index == 35) {
                    }
                }
                handlerClap?.postDelayed(this, 100L)
            }
        }

        handlerClap?.post(run)

        audioClassifier = classifier
        audioRecord = record
    }

    private fun foundPhone() {
        stopSelf()
        val intent = Intent(this, FoundPhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        super.onDestroy()
        handlerClap?.removeCallbacksAndMessages(null)
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioClassifier?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing resources: ${e.message}")
        }
        // Hiển thị lại notification khi kết thúc (nếu cần)
        val notification = createNotifyOff()
        // Tạo notification với layout ban đầu
        notificationManager.notify(1, notification)
    }
}