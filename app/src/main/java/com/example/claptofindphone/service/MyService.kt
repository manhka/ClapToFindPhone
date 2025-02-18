package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioRecord
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.RemoteViews
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.core.app.NotificationCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.FoundPhoneActivity
import com.example.claptofindphone.model.Constant
import org.tensorflow.lite.task.audio.classifier.AudioClassifier


class MyService : Service(), SensorEventListener {
    private var backInvokedCallback: OnBackInvokedCallback? = null
    private lateinit var notificationManager: NotificationManager
    private var handlerClap: Handler? = null
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var channel: NotificationChannel? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var recognizerIntent: Intent
    private lateinit var voicePasscodeSharePres: SharedPreferences
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var lightSensor: Sensor
    private var isListening = false
    private var mLastShakeTime: Long = 0
    private val SHAKE_THRESHOLD = 1000L
    private var batteryReceiver: BroadcastReceiver? = null

    private lateinit var proximitySensor: Sensor
    private var isProximityTriggered = false
    private var isPhoneInPocket = false
    private var isLightTriggered = false
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    companion object {
        private const val CHANNEL_ID = "clap_service_channel"
        private const val CHANNEL_NAME = "Clap Detection Service"
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
        runnable = Runnable {
            restartListening()
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        handlerClap = Handler()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!
    }

    fun handleBackPress(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            backInvokedCallback = OnBackInvokedCallback {}
            activity.onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT, backInvokedCallback!!
            )
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotifyOn()
        startForeground(1, notification)
        val runningService = intent?.getStringExtra(Constant.Service.RUNNING_SERVICE)
        if (runningService == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            clapAndWhistleDetect()
        } else if (runningService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            voicePasswordDetect()
        } else if (runningService == Constant.Service.TOUCH_PHONE_RUNNING) {
            mSensorManager.unregisterListener(this, lightSensor)
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else if (runningService == Constant.Service.CHARGER_ALARM_RUNNING) {
            chargerPhoneDetect()
        } else if (runningService == Constant.Service.POCKET_MODE_RUNNING) {
            mSensorManager.unregisterListener(this, mAccelerometer)
            mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            mSensorManager.registerListener(
                this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL
            )
        }
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
        return NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView).setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW).setSound(null).setVibrate(longArrayOf(0))
            .setOngoing(true).build()
    }

    @SuppressLint("RemoteViewLayout")
    private fun createNotifyOn(): Notification {
        // Inflate the custom layout using RemoteViews
        val customView = RemoteViews(packageName, R.layout.big_custom_notify2)
        val customView2 = RemoteViews(packageName, R.layout.custom_notifiy2)

        // Build and return the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Clap to find phone is running").setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView2).setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true).setSound(null)
            .setVibrate(longArrayOf(0)).build()
    }

    // clap and whistle
    private fun clapAndWhistleDetect() {
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

    // voice passcode
    private fun voicePasswordDetect() {
        voicePasscodeSharePres = getSharedPreferences(
            Constant.SharePres.VOICE_PASSCODE_SHARE_PRES, MODE_PRIVATE
        )

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("manh", "Speech recognizer not available on this device.")
            return
        }

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("manh", "Speech recognizer ready.")
            }

            override fun onBeginningOfSpeech() {
                Log.d("manh", "Listening for passcode...")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("manh", "End of speech detected.")
            }

            override fun onError(error: Int) {
                when (error) {
                    SpeechRecognizer.ERROR_NETWORK -> Log.e("manh", "Network error.")
                    SpeechRecognizer.ERROR_AUDIO -> Log.e("manh", "Audio recording error.")
                    SpeechRecognizer.ERROR_NO_MATCH -> Log.e("manh", "No match found.")
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        restartListening()
                        return
                    }
                    SpeechRecognizer.ERROR_CLIENT-> {
                        Log.e("manh", "client error")
                    }
                }
                Log.e("manh", error.toString())
                restartListening()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val passcode =
                    voicePasscodeSharePres.getString(Constant.SharePres.PASSCODE, "Hello")
                Log.d("manh", results.toString())
                matches?.let {
                    for (result in it) {
                        if (result.equals(passcode, ignoreCase = true)) {
                            foundPhone()
                            return
                        }
                    }
                }
                restartListening()
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(recognizerIntent)
    }

    private fun startListening() {
        if (!isListening) {
            isListening = true
            speechRecognizer?.startListening(recognizerIntent)
        }
    }

    private fun restartListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        }
        startListening()
    }

    // charger phone
    private fun chargerPhoneDetect() {
        batteryReceiver = object : BroadcastReceiver() {
            var isChargerPhone = false
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val status =
                        intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) // Lấy trạng thái pin
                    when (status) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> {
                            // Điện thoại đang sạc
                            isChargerPhone = true

                        }

                        BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                            // Rút sạc
                            if (isChargerPhone) {
                                isChargerPhone = false
                                foundPhone()
                            }
                        }
                    }
                }
            }
        }

        // Đăng ký Receiver cho sự kiện pin
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, intentFilter)
    }

    private fun foundPhone() {
        stopSelf()
        val intent = Intent(this, FoundPhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onSensorChanged(event: SensorEvent?) {
        // don't touch phone
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Xử lý cảm biến gia tốc (phát hiện rung lắc)
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = Math.sqrt(
                Math.pow(x.toDouble(), 2.0) + Math.pow(y.toDouble(), 2.0) + Math.pow(
                    z.toDouble(), 2.0
                )
            )
            val currentTime = System.currentTimeMillis()
            if (acceleration > 10 && currentTime - mLastShakeTime > SHAKE_THRESHOLD) {
                mLastShakeTime = currentTime
                foundPhone()
                return
            }
        }

        // pocket mode
        when (event?.sensor?.type) {
            Sensor.TYPE_PROXIMITY -> {
                // Xử lý cảm biến tiệm cận
                val proximityValue = event.values[0]
                isProximityTriggered = proximityValue < proximitySensor.maximumRange
            }

            Sensor.TYPE_LIGHT -> {
                // Xử lý cảm biến ánh sáng
                val lightValue = event.values[0]
                isLightTriggered = lightValue < 50
            }
        }
        checkPocketMode()
    }

    private fun checkPocketMode() {
        if (isProximityTriggered && isLightTriggered) {
            // phone is in the pocket
            if (!isPhoneInPocket) {
                isPhoneInPocket = true
            }
        } else {
            // phone is not in the pocket
            if (isPhoneInPocket) {
                isPhoneInPocket = false
                foundPhone()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun stopVoiceRecognition() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun stopAudioRecording() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    override fun onDestroy() {
        super.onDestroy()

        handlerClap?.removeCallbacksAndMessages(null)
        try {
            stopAudioRecording()
            if (speechRecognizer != null) {
                stopVoiceRecognition()
            }
            audioClassifier?.close()

        } catch (e: Exception) {
            Log.e(TAG, "Error releasing resources: ${e.message}")
        }
        mSensorManager.unregisterListener(this, mAccelerometer)
        mSensorManager.unregisterListener(this, lightSensor)
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver)
        }

        // Hiển thị lại notification khi kết thúc (nếu cần)
        val notification = createNotifyOff()
        // Tạo notification với layout ban đầu
        notificationManager.notify(1, notification)
    }
}