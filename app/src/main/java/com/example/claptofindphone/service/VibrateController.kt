package com.example.claptofindphone.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

class VibrateController(context: Context) {

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private var isVibrating = false


    @RequiresApi(Build.VERSION_CODES.O)
    fun startPattern(pattern: List<Long>, duration: Long) {
        isVibrating = true

        val vibrationThread = Thread {
            val vibrationPattern = pattern.toLongArray()
            val vibrationEffect = VibrationEffect.createWaveform(vibrationPattern, 0) // Repeat indefinitely
            vibrator.vibrate(vibrationEffect)

            // Stop vibration after the specified duration
            Thread.sleep(duration)
            stopVibrating()
        }
        vibrationThread.start()
    }

    /**
     * Stop the vibration.
     */
    fun stopVibrating() {
        isVibrating = false
        vibrator.cancel() // Stops any ongoing vibration
    }
}
