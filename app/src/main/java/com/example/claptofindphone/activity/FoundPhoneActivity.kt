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
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.SoundController
import com.example.claptofindphone.service.VibrateController
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils

class FoundPhoneActivity : BaseActivity() {
    private lateinit var foundPhoneBinding: ActivityFoundPhoneBinding

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
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("is_from_found_phone", true)
        startService(intent)
        SharePreferenceUtils.setIsWaited(false)
        val typeOfService = SharePreferenceUtils.getRunningService()

        if (typeOfService == Constant.Service.CLAP_AND_WHISTLE_RUNNING || typeOfService == "") {
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.found_your_phone)
        } else if (typeOfService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            SharePreferenceUtils.setRunningService("")
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.found_your_phone)
        } else if (typeOfService == Constant.Service.POCKET_MODE_RUNNING) {
            SharePreferenceUtils.setRunningService("")
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.found_your_phone)
        } else if (typeOfService == Constant.Service.CHARGER_ALARM_RUNNING) {
            SharePreferenceUtils.setRunningService("")
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.charger_alert)
            foundPhoneBinding.iFoundItButton.text=getString(R.string.turn_off)
        } else if (typeOfService == Constant.Service.TOUCH_PHONE_RUNNING) {
            SharePreferenceUtils.setRunningService("")
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.dont_touch_my_phone)
        }
        name = SharePreferenceUtils.getName()
        phone = SharePreferenceUtils.getPhone()
        themeName = SharePreferenceUtils.getThemeName()
        defaultThemeList = InstallData.getDefaultThemeList()
        callThemeList = InstallData.getCallThemeList()
        val selectedPosition = callThemeList.indexOfFirst { it.themeName == themeName }
        if (selectedPosition == -1) {
            val selectedPosition = defaultThemeList.indexOfFirst { it.themeName == themeName }
            val defaultTheme = defaultThemeList.get(selectedPosition)
            foundPhoneBinding.txtTitleFoundPhone.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            // default theme
            foundPhoneBinding.round4.visibility = View.VISIBLE
            foundPhoneBinding.round3.visibility = View.VISIBLE
            foundPhoneBinding.round2.visibility = View.VISIBLE
            foundPhoneBinding.roundCenter.visibility = View.VISIBLE
            foundPhoneBinding.smallLeftDfTheme.visibility = View.VISIBLE
            foundPhoneBinding.bigLeftDfTheme.visibility = View.VISIBLE
            foundPhoneBinding.smallRightDfTheme.visibility = View.VISIBLE
            foundPhoneBinding.bigRightDfTheme.visibility = View.VISIBLE
            foundPhoneBinding.notifyBell.visibility = View.VISIBLE
            foundPhoneBinding.iFoundItButton.visibility = View.VISIBLE


            // call theme
            foundPhoneBinding.imgViewCallThemeRound1.visibility = View.GONE
            foundPhoneBinding.imgViewCallThemeRound2.visibility = View.GONE
            foundPhoneBinding.imgViewCallThemeProfile.visibility = View.GONE
            foundPhoneBinding.responseButton.visibility = View.GONE
            foundPhoneBinding.rejectButton.visibility = View.GONE
            foundPhoneBinding.txtName.visibility = View.GONE
            foundPhoneBinding.txtPhone.visibility = View.GONE

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
                AnimationUtils.stopAnimations(foundPhoneBinding.round3)
                AnimationUtils.stopAnimations(foundPhoneBinding.round4)
                val intent = Intent(this, MyService::class.java)
                stopService(intent)
            }
        } else {
            val callTheme = callThemeList.get(selectedPosition)
            foundPhoneBinding.txtTitleFoundPhone.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            // default theme

            foundPhoneBinding.round4.visibility = View.GONE
            foundPhoneBinding.round3.visibility = View.GONE
            foundPhoneBinding.round2.visibility = View.GONE
            foundPhoneBinding.roundCenter.visibility = View.GONE
            foundPhoneBinding.smallLeftDfTheme.visibility = View.GONE
            foundPhoneBinding.bigLeftDfTheme.visibility = View.GONE
            foundPhoneBinding.smallRightDfTheme.visibility = View.GONE
            foundPhoneBinding.bigRightDfTheme.visibility = View.GONE
            foundPhoneBinding.notifyBell.visibility = View.GONE
            foundPhoneBinding.iFoundItButton.visibility = View.GONE

            // call theme
            foundPhoneBinding.imgViewCallThemeRound1.visibility = View.VISIBLE
            foundPhoneBinding.imgViewCallThemeRound2.visibility = View.VISIBLE
            foundPhoneBinding.imgViewCallThemeProfile.visibility = View.VISIBLE
            foundPhoneBinding.responseButton.visibility = View.VISIBLE
            foundPhoneBinding.rejectButton.visibility = View.VISIBLE
            foundPhoneBinding.txtName.visibility = View.VISIBLE
            foundPhoneBinding.txtPhone.visibility = View.VISIBLE
            foundPhoneBinding.activityFoundPhone.setBackgroundResource(callTheme.callThemeBg)
            foundPhoneBinding.imgViewCallThemeRound1.setImageResource(callTheme.callThemeRound1)

            if (name==""){
             foundPhoneBinding.txtName.text=getString(R.string.name)

            }else{
                foundPhoneBinding.txtName.text=name
            }
            if (phone==""){
                foundPhoneBinding.txtPhone.text=getString(R.string.phone)

            }else{
                foundPhoneBinding.txtPhone.text = phone
            }

            AnimationUtils.applyWaveAnimation(foundPhoneBinding.imgViewCallThemeRound1)
            AnimationUtils.applyWaveAnimation(foundPhoneBinding.imgViewCallThemeRound2)
            AnimationUtils.applyShakeAnimation(foundPhoneBinding.rejectButton)
            AnimationUtils.applyShakeAnimation(foundPhoneBinding.responseButton)
            foundPhoneBinding.rejectButton.setOnClickListener {
                SharePreferenceUtils.setIsFoundPhone(false)
                AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound1)
                AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound2)
                val intent = Intent(this, MyService::class.java)
                stopService(intent)
                finish()
            }
        }
        foundPhoneBinding.responseButton.setOnClickListener {
            SharePreferenceUtils.setIsFoundPhone(false)
            AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound1)
            AnimationUtils.stopAnimations(foundPhoneBinding.imgViewCallThemeRound2)

            val intent = Intent(this, MyService::class.java)
            stopService(intent)
            finish()
        }

        foundPhoneBinding.iFoundItButton.setOnClickListener {
            SharePreferenceUtils.setIsFoundPhone(false)
            AnimationUtils.stopAnimations(foundPhoneBinding.round3)
            AnimationUtils.stopAnimations(foundPhoneBinding.round4)
            if (SharePreferenceUtils.isShowRateDialog() == 0) {
                SharePreferenceUtils.setIsShowRateDialog(1)
            }
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
            finish()
        }
    }

    override fun onBackPressed() {
    }
}