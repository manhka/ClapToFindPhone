package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioRecord
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.FoundPhoneActivity
import com.example.claptofindphone.activity.SplashActivity
import com.example.claptofindphone.activity.buildMinVersion34
import com.example.claptofindphone.activity.buildMinVersionT
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils
import net.gotev.speech.GoogleVoiceTypingDisabledException
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import kotlin.math.acos
import kotlin.math.sqrt


class MyService : Service(), SensorEventListener {

    private var g = FloatArray(3) // Mảng chứa giá trị gia tốc
    private var rp: Float = -1f   // Biến toàn cục lưu giá trị cảm biến tiệm cận
    private var rl: Float = -1f   // Biến toàn cục lưu giá trị cảm biến ánh sáng
    private var inclination: Int = -1 // Độ nghiêng
    private var pocket = 0 // Trạng thái chế độ bỏ túi
    private lateinit var notificationManager: NotificationManager
    private var handlerClap: Handler? = null
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var channel: NotificationChannel? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null
    private var mLastShakeTime: Long = 0
    private val SHAKE_THRESHOLD = 1000L
    private var isVoiceDetectListening = false
    private var isClapDetectListening = false
    private var isReceiverRegistered = false
    private lateinit var passcode: String
    private var soundVolume: Int = 80
    private var soundTimePlay: Long = 15000
    private var soundStatus: Boolean = true
    private lateinit var soundList: List<Sound>
    private lateinit var flashlightList: List<Flashlight>
    private lateinit var vibrateList: List<Vibrate>
    private lateinit var vibrateName: String
    private lateinit var flashlightName: String
    private var soundId: Int = 1
    private var selectedFlashlightPosition = 0
    private var selectedVibratePosition = 0
    private var flashlightStatus: Boolean = true
    private var vibrateStatus: Boolean = true
    private var selectedSoundPosition = 0
    private var runningService = ""
    private var isFoundPhoneInClap = false
    private lateinit var customView: RemoteViews
    private lateinit var customView2: RemoteViews
    override fun onCreate() {
        super.onCreate()
        Log.d("Dectecteddddd", "onCreate: ")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        handlerClap = Handler()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        handler = Handler()
        runnable = Runnable { voicePasswordDetect() }
        passcode = SharePreferenceUtils.getVoicePasscode()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        runningService = intent?.getStringExtra(Constant.Service.RUNNING_SERVICE).toString()
        when (runningService) {
            Constant.Service.TURN_OFF_SOUND -> {
                if (soundStatus) {
                    SoundController.stopSound()
                }
                if (flashlightStatus) {
                    FlashlightController.stopFlashing()
                }
                if (vibrateStatus) {
                    VibrateController.stopVibrating()
                }
                if (SharePreferenceUtils.getRunningService() == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
                    onNotification(false)
                    if (isFoundPhoneInClap) {
                        isFoundPhoneInClap = false
                    }
                } else {
                    stopSelf()
                }
            }

            Constant.Service.CLAP_AND_WHISTLE_RUNNING -> {
                Log.d(TAG, "onStartCommand:CLAP AND WHISTLE RUNNING")
                onService()
                SharePreferenceUtils.setIsOnService(true)
                clapAndWhistleDetect()
            }

            Constant.Service.VOICE_PASSCODE_RUNNING -> {
                Log.d(TAG, "onStartCommand:VOICE PASSCODE RUNNING")
                SharePreferenceUtils.setIsOnService(true)
                Handler(Looper.getMainLooper()).postDelayed({
                    onService()
                    voicePasswordDetect()
                }, 1000)

            }

            Constant.Service.TOUCH_PHONE_RUNNING -> {
                Log.d(TAG, "onStartCommand:TOUCH PHONE RUNNING")

                val handler = Handler()
                val runnable = Runnable {
                    onService()
                    mAccelerometer =
                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
                    mSensorManager.registerListener(
                        this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
                handler.postDelayed(runnable, 5000)

            }

            Constant.Service.CHARGER_ALARM_RUNNING -> {
                Log.d(TAG, "onStartCommand:CHARGER ALARM RUNNING")
                val handler = Handler()
                val runnable = Runnable {
                    onService()
                    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                    registerReceiver(batteryReceiver, intentFilter)
                    isReceiverRegistered = true
                }
                handler.postDelayed(runnable, 5000)
            }

            Constant.Service.POCKET_MODE_RUNNING -> {
                Log.d(TAG, "onStartCommand:POCKET MODE RUNNING")
                val handler = Handler()
                val runnable = Runnable {
                    onService()
                    proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
                    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                    lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
                    mSensorManager.registerListener(
                        this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL
                    )
                    mSensorManager.registerListener(
                        this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
                    )
                    mSensorManager.registerListener(
                        this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
                handler.postDelayed(runnable, 5000)
            }
        }

        return START_STICKY
    }

    private fun onService() {
        val notification = createNotifyOn()
        if (buildMinVersion34()) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)

        }
    }

    private fun offService() {
        val notification = createNotifyOff()
        if (buildMinVersion34()) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)

        } else {
            startForeground(1, notification)

        }
    }

    private fun createNotificationChannel() {
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
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel!!)
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun createNotifyOn(): Notification {
        // Inflate the custom layout using RemoteViews
        customView = RemoteViews(packageName, R.layout.big_custom_notify2)
        customView2 = RemoteViews(packageName, R.layout.custom_notifiy2)
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark Mode
            customView2.setTextColor(R.id.txt_noti2, resources.getColor(android.R.color.white))
            customView.setTextColor(R.id.txt_noti1, resources.getColor(android.R.color.white))
        } else {
            // Light Mode
            customView.setTextColor(R.id.txt_noti2, resources.getColor(android.R.color.black))
            customView.setTextColor(R.id.txt_noti1, resources.getColor(android.R.color.black))
        }
        val pendingIntent = getPendingIntent(false)

        // Cập nhật customView với PendingIntent mới
        customView.setOnClickPendingIntent(R.id.big_custom_notify2_on, pendingIntent)
        customView2.setOnClickPendingIntent(R.id.custom_notify2_on, pendingIntent)
        customView.setOnClickPendingIntent(R.id.power_button_noti, pendingIntent)

        // Build and return the notification
        return NotificationCompat.Builder(this, Constant.Notification.CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView2).setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true).setSound(null)
            .setVibrate(null).build()
    }

    @SuppressLint("RemoteViewLayout", "ObsoleteSdkInt")
    private fun createNotifyOff(): Notification {
        // Inflate the custom layout using RemoteViews
        val customView = RemoteViews(packageName, R.layout.custom_notify_big)
        val customView2 = RemoteViews(packageName, R.layout.custom_notify_small)
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark Mode
            customView.setTextColor(R.id.txt_clap_noti, resources.getColor(android.R.color.white))
            customView.setTextColor(
                R.id.txt_voice_passcode_noti,
                resources.getColor(android.R.color.white)
            )
            customView.setTextColor(
                R.id.txt_touch_phone_noti,
                resources.getColor(android.R.color.white)
            )
            customView.setTextColor(
                R.id.txt_pocket_mode_noti,
                resources.getColor(android.R.color.white)
            )
            customView.setTextColor(
                R.id.txt_charger_alarm_noti,
                resources.getColor(android.R.color.white)
            )
        } else {
            // Light Mode
            customView.setTextColor(R.id.txt_clap_noti, resources.getColor(android.R.color.black))
            customView.setTextColor(
                R.id.txt_voice_passcode_noti,
                resources.getColor(android.R.color.black)
            )
            customView.setTextColor(
                R.id.txt_touch_phone_noti,
                resources.getColor(android.R.color.black)
            )
            customView.setTextColor(
                R.id.txt_pocket_mode_noti,
                resources.getColor(android.R.color.black)
            )
            customView.setTextColor(
                R.id.txt_charger_alarm_noti,
                resources.getColor(android.R.color.black)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            customView.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    this,
                    12,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    this,
                    13,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    this,
                    14,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    this,
                    15,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    this,
                    1435,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            // small notify
            customView2.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    this,
                    12,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    this,
                    13,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    this,
                    14,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    this,
                    15,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    this,
                    1435,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        } else {
            customView.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    this,
                    123,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    this,
                    134,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    this,
                    145,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    this,
                    156,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    this,
                    1435,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            // small notify
            customView2.setOnClickPendingIntent(
                R.id.imgView_clap_noti, PendingIntent.getActivity(
                    this,
                    123,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CLAP_TO_FIND_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_voice_passcode_noti, PendingIntent.getActivity(
                    this,
                    134,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.VOICE_PASSCODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_touch_phone_noti, PendingIntent.getActivity(
                    this,
                    145,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.DONT_TOUCH_MY_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            customView2.setOnClickPendingIntent(
                R.id.imgView_pocket_mode_noti, PendingIntent.getActivity(
                    this,
                    156,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.POCKET_MODE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            customView2.setOnClickPendingIntent(
                R.id.imgView_charger_alarm_noti, PendingIntent.getActivity(
                    this,
                    1435,
                    Intent(this, SplashActivity::class.java).putExtra(
                        "mode",
                        Constant.Service.CHARGER_PHONE
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        // Build and return the notification
        return NotificationCompat.Builder(this, Constant.Notification.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customView2).setCustomBigContentView(customView)
            .setContentTitle("Clap to find phone")
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_LOW).setSound(null).setVibrate(null)
            .setOngoing(true).build()
    }

    // create pendingIntent
    private fun getPendingIntent(value: Boolean): PendingIntent {

        val pendingIntent = PendingIntent.getActivity(
            this, 5000, Intent(this, SplashActivity::class.java).apply {
                action = value.toString()
            }, PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    // clap and whistle
    private fun clapAndWhistleDetect() {
        Log.d(TAG, "clapAndWhistleDetect: 123")
        if (!isClapDetectListening) {
            isClapDetectListening = true
        }
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
                    if (filteredModelOutput[0].index == 56 || filteredModelOutput[0].index == 57 || filteredModelOutput[0].index == 58 || filteredModelOutput[0].index == 35) {
                        if (!isFoundPhoneInClap) {
                            isFoundPhoneInClap = true
                            foundPhone()
                        }
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
        if (!isVoiceDetectListening) {
            isVoiceDetectListening = true
        }
        try {
            Speech.init(this, packageName);
            // you must have android.permission.RECORD_AUDIO granted at this point
            Speech.getInstance().startListening(object : SpeechDelegate {
                override fun onStartOfSpeech() {
                    Log.i(TAG, "speech recognition is now active")
                }

                override fun onSpeechRmsChanged(value: Float) {
                    Log.d(TAG, "rms is now: $value")
                }

                override fun onSpeechPartialResults(results: List<String?>) {
                    val str = StringBuilder()
                    for (res in results) {
                        str.append(res).append(" ")
                    }

                    Log.i(TAG, "partial result: " + str.toString().trim { it <= ' ' })
                }

                override fun onSpeechResult(result: String) {
                    Log.i(TAG, "result: $result")
                    if (result == passcode) {
                        foundPhone()

                        runnable?.let { handler?.removeCallbacks(it) }
                    } else {
                        runnable?.let { handler?.postDelayed(it, 1000) }
                    }
                }
            })
        } catch (exc: SpeechRecognitionNotAvailable) {
            Log.e(TAG, "Speech recognition is not available on this device!")
        } catch (exc: GoogleVoiceTypingDisabledException) {
            Log.e(TAG, "Google voice typing must be enabled!")
        }
    }


    // charger phone
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> {
                        // Điện thoại đang sạc
                    }

                    BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                        // Rút sạc - Gọi hàm xử lý
                        stopChargerPhone()
                        foundPhone()

                    }
                }
            }
        }
    }

    private fun stopChargerPhone() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(batteryReceiver)
                isReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.e("detectttttt", "BroadcastReceiver đã bị hủy trước đó hoặc chưa đăng ký.")
            }
        }
    }

    private fun onNotification(value: Boolean) {
        val pendingIntent = getPendingIntent(value)
        customView.setOnClickPendingIntent(R.id.big_custom_notify2_on, pendingIntent)
        customView2.setOnClickPendingIntent(R.id.custom_notify2_on, pendingIntent)
        customView.setOnClickPendingIntent(R.id.power_button_noti, pendingIntent)
        // Tạo lại thông báo với PendingIntent đã thay đổi
        val notification = NotificationCompat.Builder(this, Constant.Notification.CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView2).setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true).setSound(null)
            .setVibrate(null).build()
        notificationManager.notify(1, notification)
    }

    private fun foundPhone() {
        onNotification(true)
        setupAndStartFoundPhoneMode()
        val intent = Intent(this, FoundPhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onSensorChanged(event: SensorEvent?) {
        // pocket mode
        if (SharePreferenceUtils.getRunningService() == Constant.Service.POCKET_MODE_RUNNING) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                g = event.values.clone() // Clone sensor values

                val normOfG = sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2])

                if (normOfG != 0f) {  // Avoid division by zero
                    g[0] /= normOfG
                    g[1] /= normOfG
                    g[2] /= normOfG
                }

                inclination = Math.round(Math.toDegrees(acos(g[2].toDouble()))).toInt()


            }

            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                rp = event.values[0]
            }

            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                rl = event.values[0]
            }

            if (rp != -1f && rl != -1f && inclination != -1) {
                detectPocketMode(rp, rl, g, inclination)
            }
        } else if (SharePreferenceUtils.getRunningService() == Constant.Service.TOUCH_PHONE_RUNNING) {
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

                }
            }
        }


    }

    private fun detectPocketMode(prox: Float, light: Float, g: FloatArray, inc: Int) {
        if ((prox < 1) && (light < 2) && (g[1] < -0.6) && (inc in 75..170)) {
            pocket = 1
            // IN POCKET
        }

        if ((prox >= 1) && (light >= 2) && (g[1] >= -0.7)) {
            if (pocket == 1) {
                pocket = 0
                foundPhone()
            }

            // OUT OF POCKET
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun stopAudioRecording() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }


    private fun setupAndStartFoundPhoneMode() {

        // Load danh sách âm thanh, đèn flash, rung
        soundList = InstallData.getListSound(this)
        flashlightList = InstallData.getFlashlightList()
        vibrateList = InstallData.getVibrateList()

        // Lấy cài đặt từ SharedPreferences
        soundId = SharePreferenceUtils.getSoundId()
        selectedSoundPosition = soundList.indexOfFirst { it.id == soundId }
        soundVolume = SharePreferenceUtils.getVolumeSound(this)
        soundTimePlay = SharePreferenceUtils.getTimeSoundPlay()
        soundStatus = SharePreferenceUtils.isOnSound()

        flashlightName = SharePreferenceUtils.getFlashName()
        selectedFlashlightPosition =
            flashlightList.indexOfFirst { it.flashlightName == flashlightName }
        flashlightStatus = SharePreferenceUtils.isOnFlash()

        vibrateName = SharePreferenceUtils.getVibrateName()
        selectedVibratePosition = vibrateList.indexOfFirst { it.vibrateName == vibrateName }
        vibrateStatus = SharePreferenceUtils.isOnVibrate()

        SharePreferenceUtils.setIsFoundPhone(true)

        // Bắt đầu phát âm thanh nếu có
        if (soundStatus && selectedSoundPosition != -1) {
            SoundController.playSoundInLoop(
                soundList[selectedSoundPosition].soundType,
                soundVolume.toFloat(),
                soundTimePlay
            )
        }

        // Bật Flash nếu được bật
        if (flashlightStatus && selectedFlashlightPosition != -1) {
            FlashlightController.startPattern(
                flashlightList[selectedFlashlightPosition].flashlightMode, Long.MAX_VALUE
            )
        }

        // Bật rung nếu được bật
        if (vibrateStatus && selectedVibratePosition != -1) {
            VibrateController.startPattern(
                vibrateList[selectedVibratePosition].vibrateMode, Long.MAX_VALUE
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        offService()
        SharePreferenceUtils.setRunningService("")
        SharePreferenceUtils.setIsOnService(false)
        if (soundStatus) {
            SoundController.stopSound()
        }
        if (flashlightStatus) {
            FlashlightController.stopFlashing()
        }
        if (vibrateStatus) {
            VibrateController.stopVibrating()
        }
        if (isClapDetectListening) {
            handlerClap?.removeCallbacksAndMessages(null)
            try {
                stopAudioRecording()
                audioClassifier?.close()

            } catch (e: Exception) {
                Log.e(TAG, "Error releasing resources: ${e.message}")
            }
        }
        if (isVoiceDetectListening) {
            isVoiceDetectListening = false
            Speech.getInstance().stopListening()
            Speech.getInstance().shutdown()
            runnable?.let { handler?.removeCallbacks(it) }
        }
        if (proximitySensor != null) {
            mSensorManager.unregisterListener(this, proximitySensor)
        }
        if (mAccelerometer != null) {
            mSensorManager.unregisterListener(this, mAccelerometer)
        }


        val notification = createNotifyOff()
        notificationManager.notify(1, notification)
    }
}