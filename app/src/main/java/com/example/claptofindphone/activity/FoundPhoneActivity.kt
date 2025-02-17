package com.example.claptofindphone.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityFoundPhoneBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.service.FlashlightController
import com.example.claptofindphone.service.SoundController
import com.example.claptofindphone.service.VibrateController

class FoundPhoneActivity : AppCompatActivity() {
    private lateinit var foundPhoneBinding: ActivityFoundPhoneBinding
    private lateinit var soundSharedPreferences: SharedPreferences
    private lateinit var flashlightSharedPreferences: SharedPreferences
    private lateinit var vibrateSharedPreferences: SharedPreferences
    private lateinit var soundList:List<Sound>
    private lateinit var flashlightList: List<Flashlight>
    private lateinit var vibrateList: List<Vibrate>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foundPhoneBinding=ActivityFoundPhoneBinding.inflate(layoutInflater)
        setContentView(foundPhoneBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_found_phone)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getListSound()
        getFlashlightList()
        getVibrateList()
        val soundController=SoundController(this)
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        val flashlightController = FlashlightController(cameraManager, cameraId)
        val vibrateController=VibrateController(this)
        // sound
        soundSharedPreferences=getSharedPreferences(Constant.SharePres.SOUND_SHARE_PRES,
            MODE_PRIVATE)
        val soundName=soundSharedPreferences.getString(Constant.SharePres.ACTIVE_SOUND_NAME,Constant.Sound.CAT)
        var selectedSoundPosition = soundList.indexOfFirst { it.soundName == soundName }

        val soundVolume= soundSharedPreferences.getInt(Constant.SharePres.SOUND_VOLUME,50)
        val soundTimePlay=soundSharedPreferences.getLong(Constant.SharePres.TIME_SOUND_PLAY, 15 * 1000)
        val soundStatus= soundSharedPreferences.getBoolean(Constant.SharePres.SOUND_STATUS, true)

        // flashlight
        flashlightSharedPreferences=getSharedPreferences(Constant.SharePres.FLASHLIGHT_SHARE_PRES,
            MODE_PRIVATE)
        val flashlightName=flashlightSharedPreferences.getString(
            Constant.SharePres.ACTIVE_FLASHLIGHT_NAME,
            Constant.Flashlight.default
        )
        var selectedFlashlightPosition = flashlightList.indexOfFirst { it.flashlightName == flashlightName }
        val flashlightStatus=flashlightSharedPreferences.getBoolean(Constant.SharePres.FLASHLIGHT_STATUS, true)
        // vibrate
        vibrateSharedPreferences=getSharedPreferences(Constant.SharePres.VIBRATE_SHARE_PRES,
            MODE_PRIVATE)
        val vibrateName=vibrateSharedPreferences.getString(
            Constant.SharePres.ACTIVE_VIBRATE_NAME,
            Constant.Vibrate.default
        )
        var selectedVibratePosition = vibrateList.indexOfFirst { it.vibrateName == vibrateName }
        val vibrateStatus=vibrateSharedPreferences.getBoolean(Constant.SharePres.VIBRATE_STATUS, true)
        if (soundStatus){
            soundController.playSoundInLoop(soundList.get(selectedSoundPosition).soundType,soundVolume.toFloat(),soundTimePlay)
        }
        if (flashlightStatus){
            flashlightController.startPattern(flashlightList.get(selectedFlashlightPosition).flashlightMode,soundTimePlay)
        }
        if (vibrateStatus){
            vibrateController.startPattern(vibrateList.get(selectedVibratePosition).vibrateMode,soundTimePlay)
        }
        foundPhoneBinding.iFoundItButton.setOnClickListener {
            if (soundStatus){
                soundController.stopSound()
            }
            if (flashlightStatus){
                flashlightController.stopFlashing()
            }
            if (vibrateStatus){
                vibrateController.stopVibrating()
            }
            val intent= Intent(this,SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun getListSound() {
        soundList = listOf(
            Sound(
                Constant.Sound.CAT,
                R.drawable.cat_meowing_ic,
                R.drawable.bg_sound_passive,
                R.raw.cat_meowing
            ),
            Sound(
                Constant.Sound.DOG,
                R.drawable.dog_barking_ic,
                R.drawable.bg_sound_passive,
                R.raw.dog_barking
            ),
            Sound(
                Constant.Sound.HEY_STAY_HERE,
                R.drawable.hey_stay_here_ic,
                R.drawable.bg_sound_passive, R.raw.stay_here
            ),
            Sound(
                Constant.Sound.WHISTLE,
                R.drawable.whistle_ic,
                R.drawable.bg_sound_passive,
                R.raw.whistle
            ),
            Sound(
                Constant.Sound.HELLO,
                R.drawable.hello_ic,
                R.drawable.bg_sound_passive,
                R.raw.hello
            ),
            Sound(
                Constant.Sound.CAR_HONK,
                R.drawable.car_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.car_honk
            ),
            Sound(
                Constant.Sound.DOOR_BELL,
                R.drawable.door_bell_ic,
                R.drawable.bg_sound_passive,
                R.raw.door_bell
            ),
            Sound(
                Constant.Sound.PARTY_HORN,
                R.drawable.party_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.party_horn
            ),
            Sound(
                Constant.Sound.POLICE_WHISTLE,
                R.drawable.police_whistle_ic,
                R.drawable.bg_sound_passive, R.raw.police_whistle
            ),
            Sound(
                Constant.Sound.CAVARLY,
                R.drawable.cavalry_ic,
                R.drawable.bg_sound_passive,
                R.raw.cavalry
            ),
            Sound(
                Constant.Sound.ARMY_TRUMPET,
                R.drawable.army_trumpet_ic,
                R.drawable.bg_sound_passive, R.raw.army_trumpet
            ),
            Sound(
                Constant.Sound.RIFLE,
                R.drawable.rifle_ic,
                R.drawable.bg_sound_passive,
                R.raw.rifle
            ),
        )
    }

    private fun getFlashlightList() {
        flashlightList = listOf(
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.default,
                0,
                R.drawable.active_theme_ic,
                listOf(0L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight1,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(300L, 300L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight2,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(500L, 500L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight3,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(200L, 100L, 200L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight4,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(1000L, 500L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight5,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(300L, 300L, 600L, 300L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight6,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(150L, 150L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight7,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(700L, 300L, 100L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight8,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(500L, 100L, 200L, 300L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight9,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(1000L, 1000L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight10,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(100L, 100L, 100L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight11,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(400L, 200L, 600L)
            ),
        )
    }

    private fun getVibrateList() {
        vibrateList= listOf(
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.default,0,R.drawable.active_theme_ic,listOf(0L, 100L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate1,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 200L, 100L, 300L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate2,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 300L, 100L, 500L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate3,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 400L, 200L, 400L, 200L, 600L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate4,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 500L, 100L, 300L, 200L, 500L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate5,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 600L, 200L, 400L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate6,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 700L, 100L, 100L, 100L, 700L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate7,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 200L, 300L, 200L, 300L, 400L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate8,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 300L, 100L, 200L, 100L, 300L, 200L, 500L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate9,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 400L, 100L, 300L, 100L, 400L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate10,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 500L, 100L, 200L, 300L, 600L)),
            Vibrate(R.drawable.bg_passive_item,Constant.Vibrate.vibrate11,R.drawable.ic_premium,R.drawable.active_theme_ic,listOf(0L, 600L, 200L, 400L, 200L, 300L)),
        )
    }
}