package com.example.claptofindphone.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieDrawable
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivitySetupPasscodeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class SetupVoicePasscodeActivity : BaseActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var passcode : String
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var setupPasscodeBinding: ActivitySetupPasscodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPasscodeBinding = ActivitySetupPasscodeBinding.inflate(layoutInflater)
        setContentView(setupPasscodeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passcode=SharePreferenceUtils.getVoicePasscode()
        setupPasscodeBinding.micButton.setOnClickListener {
            setupPasscodeBinding.txtTapToDo.visibility = View.GONE
            setupPasscodeBinding.micButton.visibility = View.GONE
            setupPasscodeBinding.dotLine.visibility=View.GONE
            setupPasscodeBinding.animationSoundPlay.visibility=View.VISIBLE
            setupPasscodeBinding.animationSoundPlay.apply {
                repeatMode = LottieDrawable.RESTART
                repeatCount = -1
                playAnimation()
            }
            initializeSpeechRecognizer()
        }

        // install text to speech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.ENGLISH
            } else {
                Log.e("PassCodeActivity", "TextToSpeech initialization failed.")
            }
        }
        setupPasscodeBinding.voiceButton.setOnClickListener {
            textToSpeech.speak(passcode, TextToSpeech.QUEUE_FLUSH, null, null)
            Log.d("sfasdf",passcode)
        }
        setupPasscodeBinding.saveButton.setOnClickListener {
            SharePreferenceUtils.setVoicePasscode(passcode)
            val intent= Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        setupPasscodeBinding.backButton.setOnClickListener {
            finish()
        }
    }

    // initial speech recognizer
  fun initializeSpeechRecognizer() {
        Log.d("SpeechRecognizer", "Initializing...")

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SpeechRecognizer", "Speech recognition not available on this device.")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("SpeechRecognizer", "Ready for speech input.")
                }

                override fun onBeginningOfSpeech() {
                    Log.d("SpeechRecognizer", "Speech input detected.")
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    setupPasscodeBinding.txtPasscodeResult.visibility = View.VISIBLE
                    setupPasscodeBinding.txtTapToDo.visibility=View.VISIBLE
                    setupPasscodeBinding.micButton.visibility=View.VISIBLE
                    setupPasscodeBinding.dotLine.visibility=View.GONE
                    setupPasscodeBinding.animationSoundPlay.visibility=View.GONE
                    setupPasscodeBinding.animationSoundPlay.cancelAnimation()
                }

                override fun onError(error: Int) {
//                    Log.e("SpeechRecognizer", "Error: $error")
                    setupPasscodeBinding.txtPasscodeResult.visibility=View.GONE
                    setupPasscodeBinding.dotLine.visibility=View.VISIBLE
                    setupPasscodeBinding.animationSoundPlay.visibility=View.GONE
                    setupPasscodeBinding.txtTapToDo.visibility=View.VISIBLE
                    setupPasscodeBinding.micButton.visibility=View.VISIBLE
                    setupPasscodeBinding.saveButton.visibility=View.GONE
                    setupPasscodeBinding.voiceButton.visibility=View.GONE
                    runOnUiThread {
                        Toast.makeText(applicationContext, getString(R.string.repeat), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: Bundle?) {
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val partialText = matches?.joinToString(" ") ?: ""
                    setupPasscodeBinding.txtPasscodeResult.text = partialText
                    passcode=partialText
                    if (partialText.isNotEmpty()){
                        setupPasscodeBinding.saveButton.visibility=View.VISIBLE
                        setupPasscodeBinding.voiceButton.visibility=View.VISIBLE
                    }else{
                        setupPasscodeBinding.saveButton.visibility=View.GONE
                        setupPasscodeBinding.voiceButton.visibility=View.GONE
                    }

                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        speechRecognizer.startListening(recognizerIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}