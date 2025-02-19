package com.example.claptofindphone.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityFoundPhoneBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.FlashlightController
import com.example.claptofindphone.service.SoundController
import com.example.claptofindphone.service.VibrateController

class FoundPhoneActivity : AppCompatActivity() {
    private lateinit var foundPhoneBinding: ActivityFoundPhoneBinding
    private lateinit var soundSharedPreferences: SharedPreferences
    private lateinit var flashlightSharedPreferences: SharedPreferences
    private lateinit var vibrateSharedPreferences: SharedPreferences
    private lateinit var soundList: List<Sound>
    private lateinit var flashlightList: List<Flashlight>
    private lateinit var vibrateList: List<Vibrate>
    private lateinit var themeSharedPreferences: SharedPreferences
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var themeName: String
    private lateinit var defaultThemeList: List<DefaultTheme>
    private lateinit var callThemeList: List<CallTheme>

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foundPhoneBinding = ActivityFoundPhoneBinding.inflate(layoutInflater)
        setContentView(foundPhoneBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_found_phone)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        themeSharedPreferences = getSharedPreferences(
            Constant.SharePres.THEME_SHARE_PRES,
            MODE_PRIVATE
        )
        name = themeSharedPreferences.getString(Constant.SharePres.NAME, getString(R.string.name))
            .toString()
        phone =
            themeSharedPreferences.getString(Constant.SharePres.PHONE, getString(R.string.phone))
                .toString()
        themeName = themeSharedPreferences.getString(
            Constant.SharePres.ACTIVE_THEME_NAME,
            Constant.DefaultTheme.DefaultTheme1
        )
            .toString()

        getListSound()
        getFlashlightList()
        getVibrateList()
        getDefaultThemeList()
        getCallThemeList()
        val soundController = SoundController(this)
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        val flashlightController = FlashlightController(cameraManager, cameraId)
        val vibrateController = VibrateController(this)
        // sound
        soundSharedPreferences = getSharedPreferences(
            Constant.SharePres.SOUND_SHARE_PRES,
            MODE_PRIVATE
        )
        val soundName = soundSharedPreferences.getString(
            Constant.SharePres.ACTIVE_SOUND_NAME,
            getString(Constant.Sound.CAT)
        )
        var selectedSoundPosition = soundList.indexOfFirst { it.soundName == soundName }

        val soundVolume = soundSharedPreferences.getInt(Constant.SharePres.SOUND_VOLUME, 50)
        val soundTimePlay =
            soundSharedPreferences.getLong(Constant.SharePres.TIME_SOUND_PLAY, 15 * 1000)
        val soundStatus = soundSharedPreferences.getBoolean(Constant.SharePres.SOUND_STATUS, true)

        // flashlight
        flashlightSharedPreferences = getSharedPreferences(
            Constant.SharePres.FLASHLIGHT_SHARE_PRES,
            MODE_PRIVATE
        )
        val flashlightName = flashlightSharedPreferences.getString(
            Constant.SharePres.ACTIVE_FLASHLIGHT_NAME,
            Constant.Flashlight.default
        )
        var selectedFlashlightPosition =
            flashlightList.indexOfFirst { it.flashlightName == flashlightName }
        val flashlightStatus =
            flashlightSharedPreferences.getBoolean(Constant.SharePres.FLASHLIGHT_STATUS, true)
        // vibrate
        vibrateSharedPreferences = getSharedPreferences(
            Constant.SharePres.VIBRATE_SHARE_PRES,
            MODE_PRIVATE
        )
        val vibrateName = vibrateSharedPreferences.getString(
            Constant.SharePres.ACTIVE_VIBRATE_NAME,
            Constant.Vibrate.default
        )
        var selectedVibratePosition = vibrateList.indexOfFirst { it.vibrateName == vibrateName }
        val vibrateStatus =
            vibrateSharedPreferences.getBoolean(Constant.SharePres.VIBRATE_STATUS, true)
        val selectedPosition = callThemeList.indexOfFirst { it.themeName == themeName }
        if (selectedPosition==-1){
            val selectedPosition = defaultThemeList.indexOfFirst { it.themeName == themeName }
            val defaultTheme= defaultThemeList.get(selectedPosition)
            foundPhoneBinding.txtTitleFoundPhone.setTextColor(ContextCompat.getColor(this, R.color.black))
            // default theme
            foundPhoneBinding.round4.visibility=View.VISIBLE
            foundPhoneBinding.round3.visibility=View.VISIBLE
            foundPhoneBinding.round2.visibility=View.VISIBLE
            foundPhoneBinding.roundCenter.visibility=View.VISIBLE
            foundPhoneBinding.smallLeftDfTheme.visibility=View.VISIBLE
            foundPhoneBinding.bigLeftDfTheme.visibility=View.VISIBLE
            foundPhoneBinding.smallRightDfTheme.visibility=View.VISIBLE
            foundPhoneBinding.bigRightDfTheme.visibility=View.VISIBLE
            foundPhoneBinding.notifyBell.visibility=View.VISIBLE
            foundPhoneBinding.iFoundItButton.visibility=View.VISIBLE


            // call theme
            foundPhoneBinding.imgViewCallThemeRound1.visibility= View.GONE
            foundPhoneBinding.imgViewCallThemeRound2.visibility= View.GONE
            foundPhoneBinding.imgViewCallThemeProfile.visibility=View.GONE
            foundPhoneBinding.responseButton.visibility=View.GONE
            foundPhoneBinding.rejectButton.visibility=View.GONE
            foundPhoneBinding.txtName.visibility=View.GONE
            foundPhoneBinding.txtPhone.visibility=View.GONE

            foundPhoneBinding.activityFoundPhone.setBackgroundResource(defaultTheme.defaultThemeBg)
            foundPhoneBinding.round4.setImageResource(defaultTheme.defaultThemeRound4)
            foundPhoneBinding.round3.setImageResource(defaultTheme.defaultThemeRound3)
            foundPhoneBinding.round2.setImageResource(defaultTheme.defaultThemeRound2)
            foundPhoneBinding.smallLeftDfTheme.setImageResource(defaultTheme.defaultThemeSmallLeft)
            foundPhoneBinding.bigLeftDfTheme.setImageResource(defaultTheme.defaultThemeBigLeft)
            foundPhoneBinding.smallRightDfTheme.setImageResource(defaultTheme.defaultThemeSmallRight)
            foundPhoneBinding.bigRightDfTheme.setImageResource(defaultTheme.defaultThemeBigRight)
            foundPhoneBinding.notifyBell.setImageResource(defaultTheme.defaultThemeBell)
            AnimationUtils.applyWaveAnimation(foundPhoneBinding.round3)
            foundPhoneBinding.round3.postDelayed({
                AnimationUtils.applyWaveAnimation(foundPhoneBinding.round4)
            }, 500)
            foundPhoneBinding.iFoundItButton.setOnClickListener {
                if (soundStatus) {
                    soundController.stopSound()
                }
                if (flashlightStatus) {
                    flashlightController.stopFlashing()
                }
                if (vibrateStatus) {
                    vibrateController.stopVibrating()
                }
                AnimationUtils.stopAnimations(foundPhoneBinding.round3)
                AnimationUtils.stopAnimations(foundPhoneBinding.round4)
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            val callTheme= callThemeList.get(selectedPosition)
            foundPhoneBinding.txtTitleFoundPhone.setTextColor(ContextCompat.getColor(this, R.color.white))
            // default theme

            foundPhoneBinding.round4.visibility=View.GONE
            foundPhoneBinding.round3.visibility=View.GONE
            foundPhoneBinding.round2.visibility=View.GONE
            foundPhoneBinding.roundCenter.visibility=View.GONE
            foundPhoneBinding.smallLeftDfTheme.visibility=View.GONE
            foundPhoneBinding.bigLeftDfTheme.visibility=View.GONE
            foundPhoneBinding.smallRightDfTheme.visibility=View.GONE
            foundPhoneBinding.bigRightDfTheme.visibility=View.GONE
            foundPhoneBinding.notifyBell.visibility=View.GONE
            foundPhoneBinding.iFoundItButton.visibility=View.GONE

            // call theme
            foundPhoneBinding.imgViewCallThemeRound1.visibility= View.VISIBLE
            foundPhoneBinding.imgViewCallThemeRound2.visibility= View.VISIBLE
            foundPhoneBinding.imgViewCallThemeProfile.visibility=View.VISIBLE
            foundPhoneBinding.responseButton.visibility=View.VISIBLE
            foundPhoneBinding.rejectButton.visibility=View.VISIBLE
            foundPhoneBinding.txtName.visibility=View.VISIBLE
            foundPhoneBinding.txtPhone.visibility=View.VISIBLE
            foundPhoneBinding.activityFoundPhone.setBackgroundResource(callTheme.callThemeBg)
            foundPhoneBinding.imgViewCallThemeRound1.setImageResource(callTheme.callThemeRound1)
            foundPhoneBinding.txtName.text=name
            foundPhoneBinding.txtPhone.text=phone
            AnimationUtils.applyWaveAnimation(foundPhoneBinding.imgViewCallThemeRound1)
            AnimationUtils.applyWaveAnimation(foundPhoneBinding.imgViewCallThemeRound2)
            AnimationUtils.applyShakeAnimation(foundPhoneBinding.rejectButton)
            AnimationUtils.applyShakeAnimation(foundPhoneBinding.responseButton)
            foundPhoneBinding.rejectButton.setOnClickListener {

                    if (soundStatus) {
                        soundController.stopSound()
                    }
                    if (flashlightStatus) {
                        flashlightController.stopFlashing()
                    }
                    if (vibrateStatus) {
                        vibrateController.stopVibrating()
                    }
                    AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound1)
                    AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound2)
                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)
                    finish()

            }
            foundPhoneBinding.responseButton.setOnClickListener {
                    if (soundStatus) {
                        soundController.stopSound()
                    }
                    if (flashlightStatus) {
                        flashlightController.stopFlashing()
                    }
                    if (vibrateStatus) {
                        vibrateController.stopVibrating()
                    }
                AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound1)
                AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound2)
                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)
                    finish()

            }
        }


        if (soundStatus) {
            soundController.playSoundInLoop(
                soundList.get(selectedSoundPosition).soundType,
                soundVolume.toFloat(),
                soundTimePlay
            )
        }
        if (flashlightStatus) {
            flashlightController.startPattern(
                flashlightList.get(selectedFlashlightPosition).flashlightMode,
                soundTimePlay
            )
        }
        if (vibrateStatus) {
            vibrateController.startPattern(
                vibrateList.get(selectedVibratePosition).vibrateMode,
                soundTimePlay
            )
        }
        foundPhoneBinding.iFoundItButton.setOnClickListener {
            if (soundStatus) {
                soundController.stopSound()
            }
            if (flashlightStatus) {
                flashlightController.stopFlashing()
            }
            if (vibrateStatus) {
                vibrateController.stopVibrating()
            }
            AnimationUtils.stopAnimations(foundPhoneBinding.round3)
            AnimationUtils.stopAnimations(foundPhoneBinding.round4)
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getListSound() {
        soundList = listOf(
            Sound(
                getString(Constant.Sound.CAT),
                R.drawable.cat_meowing_ic,
                R.drawable.bg_sound_passive,
                R.raw.cat_meowing
            ),
            Sound(
                getString(Constant.Sound.DOG),
                R.drawable.dog_barking_ic,
                R.drawable.bg_sound_passive,
                R.raw.dog_barking
            ),
            Sound(
                getString(Constant.Sound.HEY_STAY_HERE),
                R.drawable.hey_stay_here_ic,
                R.drawable.bg_sound_passive, R.raw.stay_here
            ),
            Sound(
                getString(Constant.Sound.WHISTLE),
                R.drawable.whistle_ic,
                R.drawable.bg_sound_passive,
                R.raw.whistle
            ),
            Sound(
                getString(Constant.Sound.HELLO),
                R.drawable.hello_ic,
                R.drawable.bg_sound_passive,
                R.raw.hello
            ),
            Sound(
                getString(Constant.Sound.CAR_HONK),
                R.drawable.car_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.car_honk
            ),
            Sound(
                getString(Constant.Sound.DOOR_BELL),
                R.drawable.door_bell_ic,
                R.drawable.bg_sound_passive,
                R.raw.door_bell
            ),
            Sound(
                getString(Constant.Sound.PARTY_HORN),
                R.drawable.party_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.party_horn
            ),
            Sound(
                getString(Constant.Sound.POLICE_WHISTLE),
                R.drawable.police_whistle_ic,
                R.drawable.bg_sound_passive, R.raw.police_whistle
            ),
            Sound(
                getString(Constant.Sound.CAVALRY),
                R.drawable.cavalry_ic,
                R.drawable.bg_sound_passive,
                R.raw.cavalry
            ),
            Sound(
                getString(Constant.Sound.ARMY_TRUMPET),
                R.drawable.army_trumpet_ic,
                R.drawable.bg_sound_passive, R.raw.army_trumpet
            ),
            Sound(
                getString(Constant.Sound.RIFLE),
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
        vibrateList = listOf(
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.default,
                0,
                R.drawable.active_theme_ic,
                listOf(0L, 100L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate1,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 200L, 100L, 300L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate2,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 300L, 100L, 500L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate3,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 400L, 200L, 400L, 200L, 600L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate4,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 500L, 100L, 300L, 200L, 500L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate5,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 600L, 200L, 400L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate6,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 700L, 100L, 100L, 100L, 700L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate7,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 200L, 300L, 200L, 300L, 400L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate8,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 300L, 100L, 200L, 100L, 300L, 200L, 500L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate9,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 400L, 100L, 300L, 100L, 400L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate10,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 500L, 100L, 200L, 300L, 600L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate11,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 600L, 200L, 400L, 200L, 300L)
            ),
        )
    }

    private fun getCallThemeList() {
        callThemeList = listOf(
            CallTheme(
                Constant.CallTheme.CallTheme1,
                R.drawable.bg_call_theme1,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme2,
                R.drawable.bg_call_theme2,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme3,
                R.drawable.bg_call_theme3,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme4,
                R.drawable.bg_call_theme4,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme5,
                R.drawable.bg_call_theme5,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme6,
                R.drawable.bg_call_theme6,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme7,
                R.drawable.bg_call_theme7,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme8,
                R.drawable.bg_call_theme8,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme9,
                R.drawable.bg_call_theme9,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme10,
                R.drawable.bg_call_theme10,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            )
        )
    }

    private fun getDefaultThemeList() {
        defaultThemeList = listOf(
            DefaultTheme(
                Constant.DefaultTheme.DefaultTheme1,
                R.color.bg_df_theme1,
                R.drawable.round4_df_theme1,
                R.drawable.round3_theme_df1,
                R.drawable.round2_theme_df1,
                R.drawable.round_center_theme_df1,
                R.drawable.small_left_theme_df1,
                R.drawable.big_left_theme_df1,
                R.drawable.small_right_theme_df1,
                R.drawable.big_right_theme_df1,
                R.drawable.bell_theme_df1,
                R.drawable.active_theme_ic
            ), DefaultTheme(
                Constant.DefaultTheme.DefaultTheme2,
                R.color.bg_df_theme2, R.drawable.round4_df_theme2, R.drawable.round3_theme_df2,
                R.drawable.round2_theme_df2, R.drawable.round_center_theme_df2,
                R.drawable.small_left_theme_df2, R.drawable.big_left_theme_df2,
                R.drawable.small_right_theme_df2, R.drawable.big_right_theme_df2,
                R.drawable.bell_theme_df2, 0
            ),

            DefaultTheme(
                Constant.DefaultTheme.DefaultTheme3,
                R.color.bg_df_theme3, R.drawable.round3_theme_df3, R.drawable.round3_theme_df3,
                R.drawable.round2_theme_df3, R.drawable.round_center_theme_df3,
                R.drawable.small_left_theme_df3, R.drawable.big_left_theme_df3,
                R.drawable.small_right_theme_df3, R.drawable.big_right_theme_df3,
                R.drawable.bell_theme_df3, 0
            ),

            DefaultTheme(
                Constant.DefaultTheme.DefaultTheme4,
                R.color.bg_df_theme4, R.drawable.round4_df_theme4, R.drawable.round3_theme_df4,
                R.drawable.round2_theme_df4, R.drawable.round_center_theme_df4,
                R.drawable.small_left_theme_df4, R.drawable.big_left_theme_df4,
                R.drawable.small_right_theme_df4, R.drawable.big_right_theme_df4,
                R.drawable.bell_theme_df4, 0
            )
        )

    }
}