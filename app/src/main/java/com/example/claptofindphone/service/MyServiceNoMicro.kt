package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.claptofindphone.activity.buildMinVersion34
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.noti.MyNotification
import com.example.claptofindphone.utils.SharePreferenceUtils
import com.example.claptofindphone.utils.SharePreferenceUtils.isWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsOnService
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsWaited
import kotlin.math.acos
import kotlin.math.sqrt

class MyServiceNoMicro : Service(), SensorEventListener {
    private var g = FloatArray(3) // Mảng chứa giá trị gia tốc
    private var rp: Float = -1f   // Biến toàn cục lưu giá trị cảm biến tiệm cận
    private var rl: Float = -1f   // Biến toàn cục lưu giá trị cảm biến ánh sáng
    private var inclination: Int = -1 // Độ nghiêng
    private var pocket = 0 // Trạng thái chế độ bỏ túi
    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null
    private var mLastShakeTime: Long = 0
    private val SHAKE_THRESHOLD = 1000L
    private var isReceiverRegistered = false
    override fun onCreate() {
        super.onCreate()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MyNotification.createNotificationChannel(this)
        val turnOnNotificationFromHome = intent?.getBooleanExtra("turnOnNotifyFromHome", false)
        if (turnOnNotificationFromHome == true) {
            offService()
        }else{
            val runningService = intent?.getStringExtra(Constant.Service.RUNNING_SERVICE).toString()
            when (runningService) {
                Constant.Service.TURN_OFF_SOUND -> {

                    WakeupPhone.turnOffEffects()
                    stopSelf()
                }

                Constant.Service.TOUCH_PHONE_RUNNING -> {
                    Log.d(TAG, "onStartCommand:TOUCH PHONE RUNNING")
                    setIsOnService(true)
                    val handler = Handler()
                    onService()
                    val runnable = Runnable {
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
                    setIsOnService(true)
                    val handler = Handler()
                    onService()
                    val runnable = Runnable {

                        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                        registerReceiver(batteryReceiver, intentFilter)
                        isReceiverRegistered = true
                    }
                    handler.postDelayed(runnable, 5000)
                }

                Constant.Service.POCKET_MODE_RUNNING -> {
                    Log.d(TAG, "onStartCommand:POCKET MODE RUNNING")
                    setIsOnService(true)
                    val handler = Handler()
                    onService()
                    val runnable = Runnable {
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
        }

        return START_STICKY
    }

    private fun onService() {
        val notification = MyNotification.createNotifyOn(this)
        if (buildMinVersion34()) {
            startForeground(1, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)

        }
    }

    private fun offService() {
            val notification = MyNotification.createNotifyOff(this)
        if (buildMinVersion34()) {
            startForeground(1, notification,FOREGROUND_SERVICE_TYPE_SPECIAL_USE )
        } else {
            startForeground(1, notification)

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
                        WakeupPhone.foundPhone(this@MyServiceNoMicro)
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
                Log.d(TAG, "stopChargerPhone: ${e}")
            }
        }
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
                Log.d(TAG, "onSensorChanged: ${acceleration}")
                if (acceleration > 10.4f && currentTime - mLastShakeTime > SHAKE_THRESHOLD) {
                    mLastShakeTime = currentTime
                    WakeupPhone.foundPhone(this@MyServiceNoMicro)
                    unRegisterSensor()
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
                WakeupPhone.foundPhone(this@MyServiceNoMicro)
                unRegisterSensor()
            }
            // OUT OF POCKET
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

private fun unRegisterSensor(){
    if (proximitySensor != null) {
        Log.d(TAG, "unRegisterSensor: proximitySensor")
        mSensorManager.unregisterListener(this, proximitySensor)
    }
    if (mAccelerometer != null) {
        Log.d(TAG, "unRegisterSensor: mAccelerometer")

        mSensorManager.unregisterListener(this, mAccelerometer)
    }
    if (lightSensor != null) {
        Log.d(TAG, "unRegisterSensor: lightSensor")

        mSensorManager.unregisterListener(this, lightSensor)
    }
}
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "===================================>onDestroy: ")
        if (isWaited()) {
            setIsWaited(false)
        }
        SharePreferenceUtils.setRunningService("")
        setIsOnService(false)
        unRegisterSensor()
        WakeupPhone.turnOffEffects()
        MyNotification.updateOffNotification(this)

    }
}