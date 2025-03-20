package com.example.claptofindphone.service

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.claptofindphone.activity.FoundPhoneActivity
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.noti.MyNotification
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils

object WakeupPhone {
    private var soundVolume: Int = 80
    private var soundTimePlay: Long = 15000
    private var soundStatus: Boolean = true
    private lateinit var soundList: List<Sound>
    private lateinit var flashlightList: List<Flashlight>
    private lateinit var vibrateList: List<Vibrate>
    private var vibrateId: Int? = null
    private var flashlightId: Int? = null
    private var soundId: Int = 1
    private var selectedFlashlightPosition = 0
    private var selectedVibratePosition = 0
    private var flashlightStatus: Boolean = true
    private var vibrateStatus: Boolean = true
    private var selectedSoundPosition = 0
    fun foundPhone(context: Context) {
        MyNotification.updateOnNotification(context, true)
        setupAndStartFoundPhoneMode(context)
        val intent = Intent(context, FoundPhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun setupAndStartFoundPhoneMode(context: Context) {
        // Load danh sách âm thanh, đèn flash, rung
        soundList = InstallData.getListSound(context)
        flashlightList = InstallData.getFlashlightList(context)
        vibrateList = InstallData.getVibrateList(context)
        // Lấy cài đặt từ SharedPreferences
        soundId = SharePreferenceUtils.getSoundId()
        selectedSoundPosition = soundList.indexOfFirst { it.id == soundId }
        soundVolume = SharePreferenceUtils.getVolumeSound(context)
        soundTimePlay = SharePreferenceUtils.getTimeSoundPlay()
        soundStatus = SharePreferenceUtils.isOnSound()

        flashlightId = SharePreferenceUtils.getFlashlightId()
        selectedFlashlightPosition =
            flashlightList.indexOfFirst { it.flashlightId == flashlightId }
        flashlightStatus = SharePreferenceUtils.isOnFlash()

        vibrateId = SharePreferenceUtils.getVibrateId()
        selectedVibratePosition = vibrateList.indexOfFirst { it.vibrateId == vibrateId }
        vibrateStatus = SharePreferenceUtils.isOnVibrate()
        SharePreferenceUtils.setIsFoundPhone(true)

        // Bắt đầu phát âm thanh nếu có
        if (soundStatus && selectedSoundPosition != -1) {
            setDeviceVolume(context,soundVolume)
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

    fun turnOffEffects() {
        if (soundStatus) {
            SoundController.stopSound()
        }
        if (flashlightStatus) {
            FlashlightController.stopFlashing()
        }
        if (vibrateStatus) {
            VibrateController.stopVibrating()
        }
    }

    private fun setDeviceVolume(context: Context, volume: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
    }
}
