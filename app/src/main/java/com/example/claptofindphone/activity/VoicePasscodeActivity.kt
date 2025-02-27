package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityVoicePasscodeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.utils.SharePreferenceUtils

class VoicePasscodeActivity : BaseActivity() {
    private lateinit var voicePasscodeBinding: ActivityVoicePasscodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharePreferenceUtils.setOpenHomeFragment(Constant.Service.VOICE_PASSCODE)
        voicePasscodeBinding = ActivityVoicePasscodeBinding.inflate(layoutInflater)
        setContentView(voicePasscodeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.voice_passcode_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        voicePasscodeBinding.voiceButton.setOnClickListener {
            val intent = Intent(this, SetupVoicePasscodeActivity::class.java)
            startActivity(intent)
        }
        voicePasscodeBinding.textButton.setOnClickListener {
            val intent = Intent(this, SetupTextPasscodeActivity::class.java)
            startActivity(intent)
        }
        voicePasscodeBinding.backButton.setOnClickListener {
            finish()
        }
    }
}