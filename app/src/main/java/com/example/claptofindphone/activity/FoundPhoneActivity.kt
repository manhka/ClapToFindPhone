package com.example.claptofindphone.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityFoundPhoneBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.MyServiceNoMicro
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils


class FoundPhoneActivity : BaseActivity() {
    private lateinit var foundPhoneBinding: ActivityFoundPhoneBinding
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var themeName: String
    private lateinit var defaultThemeList: List<DefaultTheme>
    private lateinit var callThemeList: List<CallTheme>

    @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foundPhoneBinding = ActivityFoundPhoneBinding.inflate(layoutInflater)
        setContentView(foundPhoneBinding.root)
        changeBackPressCallBack { }
        foundPhoneBinding.txtName.isSelected=true
        SharePreferenceUtils.setIsWaited(false)
        val typeOfService = SharePreferenceUtils.getRunningService()
        if (typeOfService == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.found_your_phone)
        } else if (typeOfService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.found_your_phone)
        } else if (typeOfService == Constant.Service.POCKET_MODE_RUNNING) {
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.pocket_mode_alert)
        } else if (typeOfService == Constant.Service.CHARGER_ALARM_RUNNING) {
            foundPhoneBinding.txtTitleFoundPhone.text = getString(R.string.charger_alert)
            foundPhoneBinding.iFoundItButton.text = getString(R.string.turn_off)
        } else if (typeOfService == Constant.Service.TOUCH_PHONE_RUNNING) {
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
                    this, R.color.black
                )
            )
            // default theme
            foundPhoneBinding.txtTitleFoundPhone.visibility=View.VISIBLE
            foundPhoneBinding.dfThemeLottie.visibility = View.VISIBLE
            foundPhoneBinding.bellDefaultTheme.visibility = View.VISIBLE
            foundPhoneBinding.iFoundItButton.visibility = View.VISIBLE

            // call theme
            foundPhoneBinding.callThemeLottie.visibility = View.GONE
            foundPhoneBinding.responseButton.visibility = View.GONE
            foundPhoneBinding.rejectButton.visibility = View.GONE
            foundPhoneBinding.txtName.visibility = View.GONE
            foundPhoneBinding.txtPhone.visibility = View.GONE
            foundPhoneBinding.activityFoundPhone.setBackgroundResource(defaultTheme.defaultThemeBg)
            foundPhoneBinding.dfThemeLottie.setAnimation(defaultTheme.defaultThemeLottie)
            foundPhoneBinding.bellDefaultTheme.setImageResource(defaultTheme.defaultTheme)

        } else {
            val callTheme = callThemeList.get(selectedPosition)
            // default theme
            foundPhoneBinding.txtTitleFoundPhone.visibility=View.GONE
            foundPhoneBinding.dfThemeLottie.visibility = View.GONE
            foundPhoneBinding.dfThemeLottie.visibility = View.GONE
            foundPhoneBinding.iFoundItButton.visibility = View.GONE

            // call theme
            foundPhoneBinding.callThemeLottie.visibility = View.VISIBLE
            foundPhoneBinding.responseButton.visibility = View.VISIBLE
            foundPhoneBinding.rejectButton.visibility = View.VISIBLE
            foundPhoneBinding.txtName.visibility = View.VISIBLE
            foundPhoneBinding.txtPhone.visibility = View.VISIBLE
            foundPhoneBinding.activityFoundPhone.setBackgroundResource(callTheme.callThemeBg)
            foundPhoneBinding.rejectButton.setAnimation(callTheme.callThemeRejectLottie)
            foundPhoneBinding.responseButton.setAnimation(callTheme.callThemeResponseLottie)

            if (name == "") {
                foundPhoneBinding.txtName.text = getString(R.string.name)

            } else {
                foundPhoneBinding.txtName.text = name
            }
            if (phone == "") {
                foundPhoneBinding.txtPhone.text = getString(R.string.phone)

            } else {
                foundPhoneBinding.txtPhone.text = phone
            }


        }
        foundPhoneBinding.rejectButton.setOnClickListener {
            SharePreferenceUtils.setIsFoundPhone(false)
            if (SharePreferenceUtils.getRunningService() != Constant.Service.CLAP_AND_WHISTLE_RUNNING && SharePreferenceUtils.getRunningService() != Constant.Service.VOICE_PASSCODE_RUNNING) {
                SharePreferenceUtils.setRunningService("")
                val startIntent = Intent(this, MyServiceNoMicro::class.java)
                startIntent.putExtra(
                    Constant.Service.RUNNING_SERVICE, Constant.Service.TURN_OFF_SOUND
                )
                startService(startIntent)
            } else {
//                if (SharePreferenceUtils.getRunningService() != Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
//                    SharePreferenceUtils.setRunningService("")
//                }
                val startIntent = Intent(this, MyService::class.java)
                startIntent.putExtra(
                    Constant.Service.RUNNING_SERVICE, Constant.Service.TURN_OFF_SOUND
                )
                startService(startIntent)
            }
            val navigateToSplash = Intent(this, SplashActivity::class.java)
            startActivity(navigateToSplash)
            finish()
        }
        foundPhoneBinding.responseButton.setOnClickListener {
            SharePreferenceUtils.setIsFoundPhone(false)
            if (SharePreferenceUtils.getRunningService() != Constant.Service.CLAP_AND_WHISTLE_RUNNING && SharePreferenceUtils.getRunningService() != Constant.Service.VOICE_PASSCODE_RUNNING) {
                SharePreferenceUtils.setRunningService("")
                val startIntent = Intent(this, MyServiceNoMicro::class.java)
                startIntent.putExtra(
                    Constant.Service.RUNNING_SERVICE, Constant.Service.TURN_OFF_SOUND
                )
                startService(startIntent)
            } else {
                val startIntent = Intent(this, MyService::class.java)
                startIntent.putExtra(
                    Constant.Service.RUNNING_SERVICE, Constant.Service.TURN_OFF_SOUND
                )
                startService(startIntent)
            }
            val navigateToSplash = Intent(this, SplashActivity::class.java)
            startActivity(navigateToSplash)
            finish()
        }

        foundPhoneBinding.iFoundItButton.setOnClickListener {
            SharePreferenceUtils.setIsFoundPhone(false)
            if (SharePreferenceUtils.getRunningService() != Constant.Service.CLAP_AND_WHISTLE_RUNNING && SharePreferenceUtils.getRunningService() != Constant.Service.VOICE_PASSCODE_RUNNING) {
                SharePreferenceUtils.setRunningService("")
                val startIntent = Intent(this, MyServiceNoMicro::class.java)
                startIntent.putExtra(
                    Constant.Service.RUNNING_SERVICE, Constant.Service.TURN_OFF_SOUND
                )
                startService(startIntent)
            } else {
//                if (SharePreferenceUtils.getRunningService() != Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
//                    SharePreferenceUtils.setRunningService("")
//                }
                val startIntent = Intent(this, MyService::class.java)
                startIntent.putExtra(
                    Constant.Service.RUNNING_SERVICE, Constant.Service.TURN_OFF_SOUND
                )
                startService(startIntent)
            }
            val navigateToSplash = Intent(this, SplashActivity::class.java)
            startActivity(navigateToSplash)
            finish()
        }
    }
}