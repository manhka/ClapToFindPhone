package com.example.claptofindphone.service

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

object SoundController {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(soundResId: Int, volume: Float = 1f, durationInMillis: Long = 0) {
        stopSound()
        val context = getApplicationContext()
        mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer?.apply {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volumeToSet = (maxVolume * volume / 100f).toInt()

            setVolume(volume / 100f, volume / 100f)
            start()

            setOnCompletionListener {
                stopSound()
            }

            if (durationInMillis > 0) {
                Handler(Looper.getMainLooper()).postDelayed({
                    stopSound()
                }, durationInMillis)
            }
        }
    }

    fun startMediaSilently(soundResId: Int, durationInMillis: Long = 0) {
        stopSound()
        val context = getApplicationContext()
        mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer?.apply {
            setVolume(0f, 0f)
            start()

            setOnCompletionListener {
                stopSound()
            }

            if (durationInMillis > 0) {
                Handler(Looper.getMainLooper()).postDelayed({
                    stopSound()
                }, durationInMillis)
            }
        }
    }

    fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun playSoundInLoop(soundResId: Int, volume: Float = 1f, durationInMillis: Long = 0) {
        stopSound()
        val context = getApplicationContext()
        mediaPlayer = MediaPlayer.create(context, soundResId)
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer?.apply {
                isLooping = true
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volumeToSet = (maxVolume * volume).toInt()

                setVolume(volumeToSet.toFloat() / maxVolume, volumeToSet.toFloat() / maxVolume)
                start()
            }
        }, 500)

        if (durationInMillis > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                stopSound()
            }, durationInMillis)
        }
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
