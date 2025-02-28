package com.example.claptofindphone.utils

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.util.Log
import com.example.claptofindphone.model.Constant
import java.util.Locale

object SharePreferenceUtils {

    const val PER_NAME = "data_app_shared_preference"

    lateinit var sharePref: SharedPreferences

    fun init(context: Context) {
        if (!SharePreferenceUtils::sharePref.isInitialized) {
            sharePref = context.getSharedPreferences(PER_NAME, Context.MODE_PRIVATE)
        }
    }

    fun <T> saveKey(key: String, value: T) {
        when (value) {
            is String -> sharePref.edit().putString(key, value).apply()
            is Int -> sharePref.edit().putInt(key, value).apply()
            is Boolean -> sharePref.edit().putBoolean(key, value).apply()
            is Long -> sharePref.edit().putLong(key, value).apply()
            is Float -> sharePref.edit().putFloat(key, value).apply()
        }

    }

    fun getString(key: String, value: String = ""): String {
        return sharePref.getString(key, value)?.trim() ?: value
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharePref.getInt(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharePref.getBoolean(key, defaultValue)
    }

    fun getLong(key: String): Long {
        return sharePref.getLong(key, 15000L)
    }

    fun getFloat(key: String): Float {
        return sharePref.getFloat(key, 0f)
    }

    // First time get in App
    fun getTimeComeHome(): Int = getInt("getTimeComeHome", 0)
    fun setTimeComeHome(value: Int) = saveKey("getTimeComeHome", value)

    //Show dialog fragment home
    fun isShowClapAndWhistleDialog(): Boolean = getBoolean("isShowClapAndWhistleDialog", true)
    fun setIsShowClapAndWhistleDialog(value: Boolean) = saveKey("isShowClapAndWhistleDialog", value)

    fun isShowVoicePasscodeDialog(): Boolean = getBoolean("isShowVoicePasscodeDialog", true)
    fun setIsShowVoicePasscodeDialog(value: Boolean) = saveKey("isShowVoicePasscodeDialog", value)

    fun isShowPocketModeDialog(): Boolean = getBoolean("isShowPocketModeDialog", true)
    fun setIsShowPocketModeDialog(value: Boolean) = saveKey("isShowPocketModeDialog", value)

    fun isShowChargerPhoneDialog(): Boolean = getBoolean("isShowChargerPhoneDialog", true)
    fun setIsShowChargerPhoneDialog(value: Boolean) = saveKey("isShowChargerPhoneDialog", value)

    fun isShowTouchPhoneDialog(): Boolean = getBoolean("isShowTouchPhoneDialog", true)
    fun setIsShowTouchPhoneDialog(value: Boolean) = saveKey("isShowTouchPhoneDialog", value)

    // Flashlight
    fun isOnFlash(): Boolean = getBoolean("isOnFlash", true)
    fun setOnFlash(value: Boolean) = saveKey("isOnFlash", value)
    fun getFlashName(): String = getString("getFlashName", Constant.Flashlight.default)
    fun setFlashName(value: String) = saveKey("getFlashName", value)


    // Vibrate
    fun isOnVibrate(): Boolean = getBoolean("isOnVibrate", true)
    fun setOnVibrate(value: Boolean) = saveKey("isOnVibrate", value)
    fun getVibrateName(): String = getString("getVibrateName", Constant.Vibrate.default)
    fun setVibrateName(value: String) = saveKey("getVibrateName", value)
    // Sound

    fun isOnSound(): Boolean = getBoolean("isOnSound", true)
    fun setOnSound(value: Boolean) = saveKey("isOnSound", value)
    fun getSoundId(): Int = getInt("getSoundId", 1)
    fun setSoundId(value: Int) = saveKey("getSoundId", value)
    fun getTimeSoundPlay(): Long = getLong("getTimeSoundPlay")
    fun setTimeSoundPlay(value: Long) = saveKey("getTimeSoundPlay", value)
    fun getVolumeSound(context: Context): Int {
        // Lấy giá trị âm lượng từ SharedPreferences
        var volume = getInt("getVolumeSound", -1) // Trả về -1 nếu không có giá trị lưu

        // Nếu không có giá trị lưu (lần đầu hoặc giá trị chưa được lưu), tính toán 80% max volume
        if (volume == -1) {
            // Lấy AudioManager từ hệ thống
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // Lấy giá trị âm lượng tối đa của STREAM_MUSIC
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            // Tính toán 80% của giá trị âm lượng tối đa
            volume = (maxVolume * 0.8).toInt()

            // Lưu giá trị đã tính toán vào SharedPreferences để sử dụng lần sau
            setVolumeSound(volume)
        }

        return volume
    }
    fun setVolumeSound(value: Int) = saveKey("getVolumeSound", value)


    // is premium

    fun isVibratePremiumVisible(position: Int): Boolean =
        getBoolean("isVibratePremiumVisible_$position", true)

    fun setIsVibratePremiumVisible(position: Int, value: Boolean) =
        saveKey("isVibratePremiumVisible_$position", value)

    fun isFlashlightPremiumVisible(position: Int): Boolean =
        getBoolean("isFlashlightPremiumVisible_$position", true)

    fun setIsFlashlightPremiumVisible(position: Int, value: Boolean) =
        saveKey("isFlashlightPremiumVisible_$position", value)

    fun isCallThemePremiumVisible(position: Int): Boolean =
        getBoolean("isCallThemePremiumVisible_$position", true)

    fun setIsCallThemePremiumVisible(position: Int, value: Boolean) =
        saveKey("isCallThemePremiumVisible_$position", value)

    // Language
    fun getLanguageCode(): String = getString("getLanguageCode", Locale.getDefault().language)
    fun setLanguageCode(value: String) = saveKey("getLanguageCode", value)

    // Theme
    fun getThemeName(): String = getString("getThemeName", Constant.DefaultTheme.DefaultTheme1)
    fun setThemeName(value: String) = saveKey("getThemeName", value)
    fun getName(): String = getString("getName", "")
    fun setName(value: String) = saveKey("getName", value)
    fun getPhone(): String = getString("getPhone", "")
    fun setPhone(value: String) = saveKey("getPhone", value)


    // Voice passcode
    fun getVoicePasscode(): String = getString("getVoicePasscode", "")
    fun setVoicePasscode(value: String) = saveKey("getVoicePasscode", value)

    // notification
    fun getDeniCount(): Int = getInt("getDeniCount", 0)
    fun setDeniCount(value: Int) = saveKey("getDeniCount", value)


    // wait screen
    fun isWaited(): Boolean = getBoolean("isWaited", true)
    fun setIsWaited(value: Boolean) = saveKey("isWaited", value)

    // service
    fun getRunningService(): String = getString("getRunningService", "")
    fun setRunningService(value: String) = saveKey("getRunningService", value)

    // home open fragment
    fun getOpenHomeFragment(): String =
        getString("getOpenHomeFragment", Constant.Service.CLAP_TO_FIND_PHONE)

    fun setOpenHomeFragment(value: String) = saveKey("getOpenHomeFragment", value)

    // rate after first time use service
    fun isShowRateDialog(): Int = getInt("isShowRateDialog", 0)
    fun setIsShowRateDialog(value: Int) = saveKey("isShowRateDialog", value)

    // found phone
    fun isFoundPhone(): Boolean = getBoolean("isFoundPhone", false)
    fun setIsFoundPhone(value: Boolean) = saveKey("isFoundPhone", value)

    // noti
    fun getIndexNoty(): Int = getInt("getIndexNoty", 0)
    fun setIndexNoty(value: Int) = saveKey("getIndexNoty", value)
    // permision
    fun getTimeDeniRecordPermission():Int= getInt("getTimeDeniRecordPermission",0)
    fun setTimeDeniRecordPermission(value: Int)= saveKey("getTimeDeniRecordPermission",value)

}