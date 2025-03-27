package com.example.claptofindphone.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityVoicePasscodeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils

class VoicePasscodeActivity : BaseActivity() {
    private lateinit var voicePasscodeBinding: ActivityVoicePasscodeBinding
   private val permissionController=PermissionController()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        changeBackPressCallBack {
            val intent= Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        voicePasscodeBinding = ActivityVoicePasscodeBinding.inflate(layoutInflater)
        setContentView(voicePasscodeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.voice_passcode_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        SharePreferenceUtils.setOpenHomeFragment(Constant.Service.VOICE_PASSCODE)

        voicePasscodeBinding.voiceButton.setOnClickListener {
            if (permissionController.isInternetAvailable(this)){
                if (checkPermission()){
                    val intent = Intent(this, SetupVoicePasscodeActivity::class.java)
                    startActivity(intent)
                }else{
                    requestPermission()
                }

            }else{
                Toast.makeText(
                    this,
                    getString(R.string.connect_internet_to_use_this_feature),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        voicePasscodeBinding.textButton.setOnClickListener {
            if (permissionController.isInternetAvailable(this)){
                val intent = Intent(this, SetupTextPasscodeActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(
                    this,
                    getString(R.string.connect_internet_to_use_this_feature),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        voicePasscodeBinding.backButton.setOnClickListener {
            SharePreferenceUtils.setIsNavigateToChangePasscode(false)
            SharePreferenceUtils.setRunningService("")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun checkPermission(): Boolean {
        return permissionController.hasAudioPermission(this) &&
                permissionController.isOverlayPermissionGranted(this)
    }
    private fun requestPermission() {
        permissionController.showInitialDialog(
            this,
            Constant.Permission.RECORDING_PERMISSION,
            Constant.Service.VOICE_PASSCODE,
            Constant.Service.VOICE_PASSCODE_RUNNING
        )
    }
}