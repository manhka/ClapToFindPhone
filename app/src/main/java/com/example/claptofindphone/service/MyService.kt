package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.hardware.SensorManager
import android.media.AudioRecord
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.claptofindphone.activity.buildMinVersion34
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.noti.MyNotification
import com.example.claptofindphone.utils.SharePreferenceUtils
import net.gotev.speech.GoogleVoiceTypingDisabledException
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class MyService : Service() {
    private var handlerClap: Handler? = null
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private lateinit var mSensorManager: SensorManager
    private var isVoiceDetectListening = false
    private var isClapDetectListening = false
    private lateinit var passcode: String
    private var isFoundPhone = false
    override fun onCreate() {
        super.onCreate()
        handlerClap = Handler()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        handler = Handler()
        runnable = Runnable { voicePasswordDetect() }
        passcode = SharePreferenceUtils.getVoicePasscode()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MyNotification.createNotificationChannel(this)
        val runningService = intent?.getStringExtra(Constant.Service.RUNNING_SERVICE).toString()
        when (runningService) {
            Constant.Service.TURN_OFF_SOUND -> {
                WakeupPhone.turnOffEffects()
                MyNotification.updateOnNotification(this, false)
                if (isFoundPhone) {
                    isFoundPhone = false
                }

            }

            Constant.Service.CLAP_AND_WHISTLE_RUNNING -> {
                Log.d(TAG, "onStartCommand:CLAP AND WHISTLE RUNNING")
                onService()
                SharePreferenceUtils.setIsOnService(true)
                clapAndWhistleDetect()
            }

            Constant.Service.VOICE_PASSCODE_RUNNING -> {
                Log.d(TAG, "onStartCommand:VOICE PASSCODE RUNNING")
                SharePreferenceUtils.setIsOnService(true)
                Handler(Looper.getMainLooper()).postDelayed({
                    onService()
                    voicePasswordDetect()
                }, 1000)

            }
        }
        return START_STICKY
    }

    private fun onService() {
        val notification = MyNotification.createNotifyOn(this)
        if (buildMinVersion34()) {
            startForeground(1, notification, FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)

        }
    }

    // clap and whistle
    private fun clapAndWhistleDetect() {
        if (!isClapDetectListening) {
            isClapDetectListening = true
        }
        // If the audio classifier is initialized and running, do nothing.
        if (audioClassifier != null) return
        // Initialize the audio classifier
        val classifier = AudioClassifier.createFromFile(this, "voice_detect.tflite")
        val audioTensor = classifier.createInputTensorAudio()
        // Initialize the audio recorder
        val record = classifier.createAudioRecord()
        record.startRecording()
        // Define the classification runnable
        val run = object : Runnable {
            override fun run() {
                // Load the latest audio sample
                audioTensor.load(record)
                val output = classifier.classify(audioTensor)
                // Filter out results above a certain threshold, and sort them descendingly
                val filteredModelOutput = output[0].categories.filter {
                    it.score > 0.3f
                }.sortedBy {
                    -it.score
                }

                if (filteredModelOutput.isNotEmpty()) {
                    if (filteredModelOutput[0].index == 56 || filteredModelOutput[0].index == 57 || filteredModelOutput[0].index == 58 || filteredModelOutput[0].index == 35) {
                        if (!isFoundPhone) {
                            isFoundPhone = true
                            WakeupPhone.foundPhone(this@MyService)
                        }
                    } else if (filteredModelOutput[0].index == 426 || filteredModelOutput[0].index == 479 || filteredModelOutput[0].index == 396 || filteredModelOutput[0].index == 79 || filteredModelOutput[0].index == 35) {
                    }
                }
                handlerClap?.postDelayed(this, 100L)
            }
        }

        handlerClap?.post(run)

        audioClassifier = classifier
        audioRecord = record
    }

    // voice passcode
    private fun voicePasswordDetect() {
        if (!isVoiceDetectListening) {
            isVoiceDetectListening = true
        }
        try {
            Speech.init(this, packageName);
            // you must have android.permission.RECORD_AUDIO granted at this point
            Speech.getInstance().startListening(object : SpeechDelegate {
                override fun onStartOfSpeech() {
                    Log.i(TAG, "speech recognition is now active")
                }

                override fun onSpeechRmsChanged(value: Float) {
                    Log.d(TAG, "rms is now: $value")
                }

                override fun onSpeechPartialResults(results: List<String?>) {
                    val str = StringBuilder()
                    for (res in results) {
                        str.append(res).append(" ")
                    }

                    Log.i(TAG, "partial result: " + str.toString().trim { it <= ' ' })
                }

                override fun onSpeechResult(result: String) {
                    Log.i(TAG, "result: $result")
                    if (result == passcode) {
                        Log.d(TAG, "onSpeechResult: ${isFoundPhone}")
                        if (!isFoundPhone) {
                            isFoundPhone = true
                            WakeupPhone.foundPhone(this@MyService)
                        }
                    }
                    runnable?.let { handler?.postDelayed(it, 1000) }
                }
            })
        } catch (exc: SpeechRecognitionNotAvailable) {
            Log.e(TAG, "Speech recognition is not available on this device!")
        } catch (exc: GoogleVoiceTypingDisabledException) {
            Log.e(TAG, "Google voice typing must be enabled!")
        }
    }

    private fun stopAudioRecording() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    override fun onDestroy() {
        super.onDestroy()
        SharePreferenceUtils.setRunningService("")
        SharePreferenceUtils.setIsOnService(false)
        WakeupPhone.turnOffEffects()
        if (isClapDetectListening) {
            Log.d(TAG, "onDestroy: stop clap detect")

            handlerClap?.removeCallbacksAndMessages(null)
            try {
                stopAudioRecording()
                audioClassifier?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing resources: ${e.message}")
            }
        }
        if (isVoiceDetectListening) {
            Log.d(TAG, "onDestroy: stop voice detect")
            isVoiceDetectListening = false
            Speech.getInstance().stopListening()
            Speech.getInstance().shutdown()
            runnable?.let { handler?.removeCallbacks(it) }
        }
        MyNotification.updateOffNotification(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}