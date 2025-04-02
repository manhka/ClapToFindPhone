package com.example.claptofindphone.activity

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityGrantPermissionBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.MyServiceNoMicro
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsWaited

class GrantPermissionActivity : BaseActivity() {
    private lateinit var grantPermissionBinding: ActivityGrantPermissionBinding
    private lateinit var permissionController: PermissionController
    private lateinit var typeOfPermission: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        grantPermissionBinding = ActivityGrantPermissionBinding.inflate(layoutInflater)
        setContentView(grantPermissionBinding.root)
        permissionController = PermissionController()
        checkAllPermission()
        typeOfPermission = intent.getStringExtra("typeOfPermission").toString()
        if (typeOfPermission == Constant.Permission.BOTH_PERMISSION) {
            grantPermissionBinding.recordPermissionLayout.visibility = View.VISIBLE

        } else if (typeOfPermission == Constant.Permission.OVERLAY_PERMISSION) {
            grantPermissionBinding.recordPermissionLayout.visibility = View.GONE
        }
        grantPermissionBinding.btnAllowRecordingPermission.setOnClickListener {
            permissionController.requestAudioPermission(this)
        }
        grantPermissionBinding.btnAllowOverlayPermission.setOnClickListener {
            permissionController.requestOverlayPermission(this)
        }
        grantPermissionBinding.backBtnInGrantPermission.setOnClickListener {
            SharePreferenceUtils.setRunningService("")
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkAllPermission()
        typeOfPermission = intent.getStringExtra("typeOfPermission").toString()
        if (typeOfPermission == Constant.Permission.OVERLAY_PERMISSION) {
            if (permissionController.isOverlayPermissionGranted(this)) {
                grantPermissionBinding.btnContinueInGrantPermission.setBackgroundResource(R.drawable.bg_btn_allow)
                grantPermissionBinding.btnContinueInGrantPermission.isEnabled = true

            } else {
                grantPermissionBinding.btnContinueInGrantPermission.setBackgroundResource(R.drawable.bg_btn_grey2)
                grantPermissionBinding.btnContinueInGrantPermission.isEnabled = false
            }
        } else {
            if (permissionController.hasAudioPermission(this) && permissionController.isOverlayPermissionGranted(
                    this
                )
            ) {
                grantPermissionBinding.btnContinueInGrantPermission.setBackgroundResource(R.drawable.bg_btn_allow)
                grantPermissionBinding.btnContinueInGrantPermission.isEnabled = true
            } else {
                grantPermissionBinding.btnContinueInGrantPermission.setBackgroundResource(R.drawable.bg_btn_grey2)
                grantPermissionBinding.btnContinueInGrantPermission.isEnabled = false
            }
        }
        grantPermissionBinding.btnContinueInGrantPermission.setOnClickListener {

            val typeOfRunningService = intent.getStringExtra("typeOfRunningService")
            if (typeOfRunningService=="CHANGE TEXT PASSCODE"){

                navigateToChangeTextPasscode()
            }
           else
            if (typeOfRunningService == "CHANGE VOICE PASSCODE") {
                navigateToChangeVoicePasscode()
            }else
            if(typeOfRunningService=="CHANGE_PASSCODE"){
                navigateToChangePasscode()
            }else{
                SharePreferenceUtils.setRunningService(typeOfRunningService!!)
                if (typeOfRunningService == Constant.Service.CLAP_AND_WHISTLE_RUNNING ) {
                    val intentService = Intent(this, MyService::class.java)
                    intentService.putExtra(Constant.Service.RUNNING_SERVICE, typeOfRunningService)
                    startService(intentService)
                    navigateToHome()
                }
                if (typeOfRunningService == Constant.Service.VOICE_PASSCODE_RUNNING) {
                    if (SharePreferenceUtils.getVoicePasscode() == Constant.DEFAULT_PASSCODE) {
                        navigateToChangePasscode()
                    } else {
                        val intentService = Intent(this, MyService::class.java)
                        intentService.putExtra(Constant.Service.RUNNING_SERVICE, typeOfRunningService)
                        startService(intentService)
                        navigateToHome()
                    }
                }

                if (typeOfRunningService== Constant.Service.TOUCH_PHONE_RUNNING || typeOfRunningService==Constant.Service.POCKET_MODE_RUNNING ){
                    val intentService = Intent(this, MyServiceNoMicro::class.java)
                    intentService.putExtra(Constant.Service.RUNNING_SERVICE, typeOfRunningService)
                    startService(intentService)
                    navigateToWait()
                }
                if ( typeOfRunningService==Constant.Service.CHARGER_ALARM_RUNNING){
                    if (isPlugPhone()){
                        val intentService = Intent(this, MyServiceNoMicro::class.java)
                        intentService.putExtra(Constant.Service.RUNNING_SERVICE, typeOfRunningService)
                        startService(intentService)
                        navigateToWait()
                    }else{
                        navigateToHome()
                    }
                }
            }
        }
    }

    private fun navigateToChangeVoicePasscode() {
        val intent = Intent(this, SetupVoicePasscodeActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun navigateToChangeTextPasscode() {
        val intent = Intent(this, SetupTextPasscodeActivity::class.java)
        startActivity(intent)
        finish()
    }


    // check for audio and overlay permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.Permission.AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                grantPermissionBinding.btnAllowRecordingPermission.setBackgroundResource(R.drawable.bg_btn_grey2)
                grantPermissionBinding.btnAllowRecordingPermission.isEnabled = false
            } else {
                if (SharePreferenceUtils.getTimeDeniRecordPermission() <= 3) {

                    if (SharePreferenceUtils.getTimeDeniRecordPermission() == 2) {
                        permissionController.showSettingsDialog(
                            Constant.Permission.RECORDING_PERMISSION,
                            this
                        )
                    }
                    if (SharePreferenceUtils.getTimeDeniRecordPermission() < 2) {
                        SharePreferenceUtils.setTimeDeniRecordPermission(SharePreferenceUtils.getTimeDeniRecordPermission() + 1)
                    }
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.Permission.OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                grantPermissionBinding.btnAllowOverlayPermission.setBackgroundResource(R.drawable.bg_btn_grey2)
                grantPermissionBinding.btnAllowOverlayPermission.isEnabled = false
            }
        }
    }

    // check all permission
    private fun checkAllPermission() {
        if (permissionController.hasAudioPermission(this)) {
            grantPermissionBinding.btnAllowRecordingPermission.setBackgroundResource(R.drawable.bg_btn_grey2)
            grantPermissionBinding.btnAllowRecordingPermission.isEnabled = false
        } else {
            grantPermissionBinding.btnAllowRecordingPermission.setBackgroundResource(R.drawable.bg_btn_allow)
            grantPermissionBinding.btnAllowRecordingPermission.isEnabled = true
        }
        if (permissionController.isOverlayPermissionGranted(this)) {
            grantPermissionBinding.btnAllowOverlayPermission.setBackgroundResource(R.drawable.bg_btn_grey2)
            grantPermissionBinding.btnAllowOverlayPermission.isEnabled = false
        } else {
            grantPermissionBinding.btnAllowOverlayPermission.setBackgroundResource(R.drawable.bg_btn_allow)
            grantPermissionBinding.btnAllowOverlayPermission.isEnabled = true
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToChangePasscode() {
        val intent = Intent(this, VoicePasscodeActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun navigateToWait() {
        val intent = Intent(this, WaitActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun isPlugPhone(): Boolean {
        val batteryIntent =
            registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)
    }
}