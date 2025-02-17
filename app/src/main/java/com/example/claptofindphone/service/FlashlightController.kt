package com.example.claptofindphone.service

import android.hardware.camera2.CameraManager

class FlashlightController(private val cameraManager: CameraManager, private val cameraId: String?) {
    private var isFlashing = false

     fun startPattern(pattern: List<Long>, duration :Long) {
        isFlashing = true
        val flashingThread = Thread {
            while (isFlashing) {
                pattern.forEach { duration ->
                    if (!isFlashing) return@forEach
                    toggleFlash(true)
                    Thread.sleep(duration)
                    toggleFlash(false)
                    Thread.sleep(duration)
                }
            }
        }
        flashingThread.start()


        Thread {
            Thread.sleep(duration)
            stopFlashing()
        }.start()
    }


     fun stopFlashing() {
        isFlashing = false
        toggleFlash(false)
    }

    private fun toggleFlash(isOn: Boolean) {
        try {
            cameraId?.let { cameraManager.setTorchMode(it, isOn) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}