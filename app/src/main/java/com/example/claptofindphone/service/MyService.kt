package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.app.Activity
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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureResult.SENSOR_SENSITIVITY
import android.media.AudioRecord
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.FoundPhoneActivity
import com.example.claptofindphone.activity.HomeActivity
import com.example.claptofindphone.activity.SplashActivity
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


class MyService : Service(), SensorEventListener {
    private var backInvokedCallback: OnBackInvokedCallback? = null
    private lateinit var notificationManager: NotificationManager
    private var handlerClap: Handler? = null
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var channel: NotificationChannel? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var mLastShakeTime: Long = 0
    private val SHAKE_THRESHOLD = 1000L
    private var batteryReceiver: BroadcastReceiver? = null
    private var isVoiceDetectListening = false
    private var isClapDetectListening = false
    private var proximitySensor: Sensor? = null
    private lateinit var passcode: String
    private var soundVolume: Int = 80
    private var soundTimePlay: Long = 15000
    private var soundStatus: Boolean = true
    private var soundController: SoundController? = null
    private var flashlightController: FlashlightController? = null
    private var vibrateController: VibrateController? = null
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
    private var isFromFoundPhone: Boolean = false
    private var isInPocket = false

    companion object {
        private const val CHANNEL_ID = "clap_service_channel"
        private const val CHANNEL_NAME = "Clap Detection Service"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        handlerClap = Handler()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        handler = Handler()
        runnable = Runnable { voicePasswordDetect() }
        passcode = SharePreferenceUtils.getVoicePasscode()

        soundList = InstallData.getListSound(this)
        flashlightList = InstallData.getFlashlightList()
        vibrateList = InstallData.getVibrateList()

        // sound
        soundController = SoundController(this)
        soundId = SharePreferenceUtils.getSoundId()
        selectedSoundPosition = soundList.indexOfFirst { it.id == soundId }
        soundVolume = SharePreferenceUtils.getVolumeSound(this)
        soundTimePlay = SharePreferenceUtils.getTimeSoundPlay()
        soundStatus = SharePreferenceUtils.isOnSound()
        // flashlight
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        flashlightController = FlashlightController(cameraManager, cameraId)
        flashlightName = SharePreferenceUtils.getFlashName()
        selectedFlashlightPosition =
            flashlightList.indexOfFirst { it.flashlightName == flashlightName }
        flashlightStatus = SharePreferenceUtils.isOnFlash()
        // vibrate
        vibrateController = VibrateController(this)
        vibrateName = SharePreferenceUtils.getVibrateName()
        selectedVibratePosition = vibrateList.indexOfFirst { it.vibrateName == vibrateName }
        vibrateStatus = SharePreferenceUtils.isOnVibrate()
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotifyOn()
        startForeground(1, notification)
        isFromFoundPhone = intent?.getBooleanExtra("is_from_found_phone", false) == true
        if (isFromFoundPhone == true) {
            SharePreferenceUtils.setIsFoundPhone(true)
            if (soundStatus) {
                soundController?.playSoundInLoop(
                    soundList[selectedSoundPosition].soundType,
                    soundVolume.toFloat(),
                    soundTimePlay
                )
            }
            if (flashlightStatus) {
                flashlightController?.startPattern(
                    flashlightList.get(selectedFlashlightPosition).flashlightMode,
                    Long.MAX_VALUE
                )
            }
            if (vibrateStatus) {
                vibrateController?.startPattern(
                    vibrateList.get(selectedVibratePosition).vibrateMode,
                    Long.MAX_VALUE
                )
            }
        } else {
            val runningService = intent?.getStringExtra(Constant.Service.RUNNING_SERVICE)
            Log.d("sdfasf", "onStartCommand:${runningService} ")
            if (runningService == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
                clapAndWhistleDetect()
            } else if (runningService == Constant.Service.VOICE_PASSCODE_RUNNING) {
                voicePasswordDetect()
            } else if (runningService == Constant.Service.TOUCH_PHONE_RUNNING) {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
                mSensorManager.registerListener(
                    this,
                    mAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else if (runningService == Constant.Service.CHARGER_ALARM_RUNNING) {
                chargerPhoneDetect()
            } else if (runningService == Constant.Service.POCKET_MODE_RUNNING) {
                proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!
                mSensorManager.registerListener(
                    this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL
                )
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            customView.setOnClickPendingIntent(
                R.id.power_button, PendingIntent.getActivity(
                    this, 5000, Intent(this, HomeActivity::class.java), PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            customView.setOnClickPendingIntent(
                R.id.power_button, PendingIntent.getActivity(
                    this,
                    5000,
                    Intent(this, HomeActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }

        // Build and return the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Clap to find phone is running").setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customView2).setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true).setSound(null)
            .setVibrate(longArrayOf(0)).build()
    }

    // clap and whistle
    private fun clapAndWhistleDetect() {
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
        if (!isVoiceDetectListening) {
            isVoiceDetectListening = true
        }
        try {
            Speech.init(this, packageName);
            // you must have android.permission.RECORD_AUDIO granted at this point
            Speech.getInstance().startListening(object : SpeechDelegate {
                override fun onStartOfSpeech() {
                    Log.i("speech", "speech recognition is now active")
                }

                override fun onSpeechRmsChanged(value: Float) {
                    Log.d("speech", "rms is now: $value")
                }

                override fun onSpeechPartialResults(results: List<String?>) {
                    val str = StringBuilder()
                    for (res in results) {
                        str.append(res).append(" ")
                    }

                    Log.i("speech", "partial result: " + str.toString().trim { it <= ' ' })
                }

                override fun onSpeechResult(result: String) {
                    Log.i("speech", "result: $result")
                    if (result == passcode) {
                        foundPhone()
                        runnable?.let { handler?.removeCallbacks(it) }
                    } else {
                        runnable?.let { handler?.postDelayed(it, 1000) }
                    }
                }
            })
        } catch (exc: SpeechRecognitionNotAvailable) {
            Log.e("speech", "Speech recognition is not available on this device!")
        } catch (exc: GoogleVoiceTypingDisabledException) {
            Log.e("speech", "Google voice typing must be enabled!")
        }
    }


    // charger phone
    private fun chargerPhoneDetect() {
        // Kiểm tra ngay lập tức trạng thái pin khi gọi function này
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                // Điện thoại đang sạc
            }

            BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                // Rút sạc
                foundPhone()
            }
        }
        // Đăng ký Receiver cho sự kiện pin để theo dõi tiếp
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    when (status) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> {
                            // Điện thoại đang sạc
                        }

                        BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                            // Rút sạc
                            // Chạy hành động khi điện thoại không còn sạc
                            foundPhone()  // Gọi hàm cần thiết
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
        Log.d("sensorsss", "onSensorChanged:${event?.sensor?.type} ")
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
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val proximityValue = event.values[0]
            Log.d("sdfasf", "onSensorChanged:${proximityValue} ")
            if (proximityValue.toInt() == 0) {
                isInPocket = true
            }
            if (isInPocket) {
                if (proximityValue == proximitySensor!!.maximumRange) {
                    isInPocket = false
                    mSensorManager.unregisterListener(this, proximitySensor)
                    foundPhone()
                }
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun stopAudioRecording() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("sdfasf", "onDestroy: ")
        if (soundStatus) {
            soundController?.stopSound()
        }
        if (flashlightStatus) {
            flashlightController?.stopFlashing()
        }
        if (vibrateStatus) {
            vibrateController?.stopVibrating()
        }
        if (isVoiceDetectListening) {
            isVoiceDetectListening = false
            Speech.getInstance().stopListening()
            Speech.getInstance().shutdown()
            runnable?.let { handler?.removeCallbacks(it) }
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
        if (proximitySensor != null) {
            mSensorManager.unregisterListener(this, proximitySensor)
        }
        if (mAccelerometer != null) {
            mSensorManager.unregisterListener(this, mAccelerometer)
        }

        if (isFromFoundPhone) {
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver)
        }
        // Hiển thị lại notification khi kết thúc (nếu cần)
        val notification = createNotifyOff()
        // Tạo notification với layout ban đầu
        notificationManager.notify(1, notification)

    }
}