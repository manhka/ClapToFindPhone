package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivitySetupTextPasscodeBinding
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class SetupTextPasscodeActivity : BaseActivity() {
    private lateinit var setupTextPasscodeActivity: ActivitySetupTextPasscodeBinding
    private lateinit var textToSpeech: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTextPasscodeActivity = ActivitySetupTextPasscodeBinding.inflate(layoutInflater)
        setContentView(setupTextPasscodeActivity.root)

        val permissionController=PermissionController()
        setupTextPasscodeActivity.edtTextPasscode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // call before text change
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // call when text is changing
            }
            override fun afterTextChanged(p0: Editable?) {
                // call after text changed
                if (p0 != null) {
                    if (p0.trim().isNotEmpty()) {
                        setupTextPasscodeActivity.saveButton.visibility = View.VISIBLE
                        setupTextPasscodeActivity.listenButton.visibility = View.VISIBLE

                    } else {
                        setupTextPasscodeActivity.saveButton.visibility = View.GONE
                        setupTextPasscodeActivity.listenButton.visibility = View.GONE
                    }
                }

            }

        })

        // install text to speech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.ENGLISH
            } else {
                Log.e("PassCodeActivity", "TextToSpeech initialization failed.")
            }
        }

        setupTextPasscodeActivity.listenButton.setOnClickListener {
            if (permissionController.isInternetAvailable(this)){
                val passcode = setupTextPasscodeActivity.edtTextPasscode.text.toString().trim()
                textToSpeech.speak(passcode, TextToSpeech.QUEUE_FLUSH, null, null)
            }else{
                Toast.makeText(
                    this,
                    getString(R.string.connect_internet_to_use_this_feature),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        setupTextPasscodeActivity.saveButton.setOnClickListener {
            if (permissionController.isInternetAvailable(this)){
                val passcode = setupTextPasscodeActivity.edtTextPasscode.text.toString().trim()
                SharePreferenceUtils.setVoicePasscode(passcode)
                val intent= Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(
                    this,
                    getString(R.string.connect_internet_to_use_this_feature),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        setupTextPasscodeActivity.backButton.setOnClickListener {
            finish()
        }
    }
    override fun onStop() {
        super.onStop()
        textToSpeech.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}