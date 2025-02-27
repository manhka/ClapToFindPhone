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
import net.gotev.speech.GoogleVoiceTypingDisabledException
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable
import java.util.Locale

class SetupVoicePasscodeActivity : BaseActivity() {
    private lateinit var passcode : String
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var setupPasscodeBinding: ActivitySetupPasscodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPasscodeBinding = ActivitySetupPasscodeBinding.inflate(layoutInflater)
        setContentView(setupPasscodeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setup_voice_passcode_activity)) { v, insets ->
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
        try {
            Speech.init(this, packageName);
            // you must have android.permission.RECORD_AUDIO granted at this point
            Speech.getInstance().startListening(object : SpeechDelegate {
                override fun onStartOfSpeech() {
                    Log.i("speech", "speech recognition is now active")
                }

                override fun onSpeechRmsChanged(value: Float) {
                    Log.d("speech", "rms is now: $value")
                }

                override fun onSpeechPartialResults(results: List<String?>) {
                    val str = StringBuilder()
                    for (res in results) {
                        str.append(res).append(" ")
                    }

                    Log.i("speech", "partial result: " + str.toString().trim { it <= ' ' })
                }

                override fun onSpeechResult(result: String) {
                    Log.i("speech", "result: $result")
                   if (result.trim().isEmpty() || result.trim().isBlank()){
                       setupPasscodeBinding.txtPasscodeResult.visibility=View.GONE
                       setupPasscodeBinding.dotLine.visibility=View.VISIBLE
                       setupPasscodeBinding.animationSoundPlay.visibility=View.GONE
                       setupPasscodeBinding.txtTapToDo.visibility=View.VISIBLE
                       setupPasscodeBinding.micButton.visibility=View.VISIBLE
                       setupPasscodeBinding.saveButton.visibility=View.GONE
                       setupPasscodeBinding.voiceButton.visibility=View.GONE
                       setupPasscodeBinding.saveButton.visibility=View.GONE
                       setupPasscodeBinding.voiceButton.visibility=View.GONE
                       runOnUiThread {
                           Toast.makeText(applicationContext, getString(R.string.repeat), Toast.LENGTH_SHORT).show()
                       }
                   }else{
                       setupPasscodeBinding.txtPasscodeResult.visibility = View.VISIBLE
                       setupPasscodeBinding.txtTapToDo.visibility=View.VISIBLE
                       setupPasscodeBinding.micButton.visibility=View.VISIBLE
                       setupPasscodeBinding.dotLine.visibility=View.GONE
                       setupPasscodeBinding.animationSoundPlay.visibility=View.GONE
                       setupPasscodeBinding.saveButton.visibility=View.VISIBLE
                       setupPasscodeBinding.voiceButton.visibility=View.VISIBLE
                       setupPasscodeBinding.animationSoundPlay.cancelAnimation()
                       setupPasscodeBinding.txtPasscodeResult.text = result
                       passcode=result
                   }
                }
            })
        } catch (exc: SpeechRecognitionNotAvailable) {
            Log.e("speech", "Speech recognition is not available on this device!")
        } catch (exc: GoogleVoiceTypingDisabledException) {
            Log.e("speech", "Google voice typing must be enabled!")
        }

        Log.d("SpeechRecognizer", "Initializing...")

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SpeechRecognizer", "Speech recognition not available on this device.")
            return
        }

    }
    override fun onStop() {
        super.onStop()
        textToSpeech.stop()
    }
    override fun onDestroy() {
        super.onDestroy()

        // Check if Speech instance is initialized and stop listening
        if (Speech.getInstance().isListening) {
            Speech.getInstance().stopListening()
        }

        // Check if Speech instance is not null before shutting down
        if (Speech.getInstance() != null) {
            Speech.getInstance().shutdown()
        }

        // Stop and shut down textToSpeech if initialized
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}