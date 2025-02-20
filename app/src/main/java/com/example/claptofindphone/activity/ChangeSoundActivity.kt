package com.example.claptofindphone.activity

import android.content.Intent
import android.content.SharedPreferences
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.SoundAdapter2
import com.example.claptofindphone.databinding.ActivityChangeSoundBinding
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.service.SoundController
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils

class ChangeSoundActivity : AppCompatActivity() {
    private lateinit var changeSoundBinding: ActivityChangeSoundBinding
    private lateinit var soundList: List<Sound>
    private lateinit var changeSoundAdapter: SoundAdapter2
    private lateinit var soundController: SoundController
    private lateinit var audioManager: AudioManager
    private  var selectedSoundId: Int=1
    private var timeSoundPlay: Long = 15000
    private var soundStatus: Boolean = true
    private  var soundVolume:Int=50
    private lateinit var contentObserver: ContentObserver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeSoundBinding = ActivityChangeSoundBinding.inflate(layoutInflater)
        setContentView(changeSoundBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.custom_sound_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        soundList=InstallData.getListSound(this)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        soundController = SoundController(this)

        soundStatus =SharePreferenceUtils.isOnSound()
        timeSoundPlay = SharePreferenceUtils.getTimeSoundPlay()
        soundVolume=SharePreferenceUtils.getVolumeSound()

        changeSoundBinding.seekbarVolume.max=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        changeSoundBinding.seekbarVolume.progress=soundVolume
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (soundVolume) ,
            0
        )
        contentObserver = object : ContentObserver(Handler()) {
            override fun onChange(self: Boolean) {
                val newCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                changeSoundBinding.seekbarVolume.progress = (newCurrentVolume )
            }
        }
        contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            contentObserver
        )

        changeSoundBinding.seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    (progress ),
                    0
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                soundVolume=changeSoundBinding.seekbarVolume.progress
            }
        })
        updateOnOffToggle(soundStatus)
        // sound from home
        val soundType = intent.getIntExtra("sound_type", R.raw.cat_meowing)
        val soundId = intent.getIntExtra("sound_id",1)
        if (soundId != 1) {
            selectedSoundId=soundId
        }
        soundController.playSound(soundType, 30f, 3000)
        changeSoundAdapter = SoundAdapter2(this, soundList, soundId) { sound ->
            selectedSoundId = sound.id
            soundController.playSound(sound.soundType, 30f, 3000)
        }
        changeSoundBinding.rcvCustomSoundLayout.layoutManager = GridLayoutManager(this, 3)
        changeSoundBinding.rcvCustomSoundLayout.adapter = changeSoundAdapter
        // back button
        changeSoundBinding.backButton.setOnClickListener {
            finish()
        }
        changeSoundBinding.onOffLayout.setOnClickListener {
            soundStatus = !soundStatus
            updateOnOffToggle(soundStatus)
        }
        changeSoundBinding.saveButton.setOnClickListener {
            SharePreferenceUtils.setSoundId(selectedSoundId)
            SharePreferenceUtils.setOnSound(soundStatus)
            SharePreferenceUtils.setTimeSoundPlay(timeSoundPlay)
            soundVolume=changeSoundBinding.seekbarVolume.progress
            SharePreferenceUtils.setVolumeSound(soundVolume)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        // time sound play
        changeSoundBinding.btnTime1.setOnClickListener {
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
            timeSoundPlay = 15 * 1000
        }
        changeSoundBinding.btnTime2.setOnClickListener {
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
            timeSoundPlay = 30 * 1000

        }
        changeSoundBinding.btnTime3.setOnClickListener {
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
            timeSoundPlay = 60 * 1000

        }
        changeSoundBinding.btnTime4.setOnClickListener {
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
            timeSoundPlay = 120 * 1000

        }
        changeSoundBinding.btnTime5.setOnClickListener {
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            timeSoundPlay = 1000 * 1000
        }
        updateTimeSoundPlay()
    }

    private fun updateTimeSoundPlay() {
        if (timeSoundPlay.toInt() == 15 * 1000) {
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))

        }
       else if (timeSoundPlay.toInt() == 30 * 1000) {
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
      else  if (timeSoundPlay.toInt() == 60 * 1000) {
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
       else if (timeSoundPlay.toInt() == 120 * 1000) {
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
       else{
            changeSoundBinding.btnTime5.setBackgroundResource(R.drawable.bg_active_btn)
            changeSoundBinding.btnTime2.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime3.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime4.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime1.setBackgroundResource(R.drawable.bg_passive_item)
            changeSoundBinding.btnTime5.setTextColor(ContextCompat.getColor(this,R.color.white))
            changeSoundBinding.btnTime2.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime3.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime4.setTextColor(ContextCompat.getColor(this,R.color.black))
            changeSoundBinding.btnTime1.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
    }

    private fun updateOnOffToggle(soundStatus: Boolean) {

        if (!soundStatus) {
            changeSoundBinding.offButton.visibility = View.VISIBLE
            changeSoundBinding.onButton.visibility = View.GONE
            changeSoundBinding.txtOn.visibility = View.GONE
            changeSoundBinding.txtOff.visibility = View.VISIBLE
            changeSoundBinding.onOffLayout.setBackgroundResource(R.drawable.bg_off_btn)
        } else {
            changeSoundBinding.offButton.visibility = View.GONE
            changeSoundBinding.onButton.visibility = View.VISIBLE
            changeSoundBinding.txtOn.visibility = View.VISIBLE
            changeSoundBinding.txtOff.visibility = View.GONE
            changeSoundBinding.onOffLayout.setBackgroundResource(R.drawable.bg_on_btn)
        }
    }
}