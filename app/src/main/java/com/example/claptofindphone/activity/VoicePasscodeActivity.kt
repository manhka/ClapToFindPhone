package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityVoicePasscodeBinding

class VoicePasscodeActivity : AppCompatActivity() {
    private lateinit var voicePasscodeBinding: ActivityVoicePasscodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        voicePasscodeBinding = ActivityVoicePasscodeBinding.inflate(layoutInflater)
        setContentView(voicePasscodeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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
    }
}