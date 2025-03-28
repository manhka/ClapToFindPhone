package com.example.claptofindphone.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log

object VibrateController {
    private val vibrator: Vibrator by lazy {
        getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private var isVibrating = false

    fun startPattern(pattern: List<Long>, duration: Long) {
        if (!vibrator.hasVibrator()){
            return
        } // Ensure device supports vibration
        isVibrating = true

        val vibrationThread = Thread {
            val vibrationPattern = pattern.toLongArray()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "startPattern: 1")
                val vibrationEffect = VibrationEffect.createWaveform(vibrationPattern, 0) // Repeat indefinitely
                vibrator.vibrate(vibrationEffect)
            } else {
                Log.d(TAG, "startPattern: 2")

                @Suppress("DEPRECATION")
                vibrator.vibrate(vibrationPattern, 0) // Deprecated but works for old devices
            }

            // Stop vibration after the specified duration
            Thread.sleep(duration)
            stopVibrating()
        }
        vibrationThread.start()
    }

    fun stopVibrating() {
        isVibrating = false
        vibrator.cancel() // Stops any ongoing vibration
    }

    // Get application context dynamically (no need to pass context)
    private fun getApplicationContext(): Context {
        return try {
            val appClass = Class.forName("android.app.ActivityThread")
            val method = appClass.getMethod("currentApplication")
            method.invoke(null) as Application
        } catch (e: Exception) {
            throw IllegalStateException("Unable to retrieve application context", e)
        }
    }
}
