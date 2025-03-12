package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.hardware.camera2.CameraManager


import android.content.Context

import android.app.Application


object FlashlightController {
    private var isFlashing = false

    // Lazily initialize CameraManager using applicationContext
    private val cameraManager: CameraManager by lazy {
        getApplicationContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val cameraId: String? by lazy {
        cameraManager.cameraIdList.firstOrNull()
    }

    fun startPattern(pattern: List<Long>, duration: Long) {
        if (cameraId == null) return

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

    // Helper function to get application context without passing it manually
    @SuppressLint("PrivateApi")
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

