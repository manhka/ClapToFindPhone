package com.example.claptofindphone.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.SoundAdapter2
import com.example.claptofindphone.databinding.ActivityChangeSoundBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.service.SoundController

class ChangeSoundActivity : AppCompatActivity() {
    private lateinit var changeSoundBinding: ActivityChangeSoundBinding
    private lateinit var soundList: List<Sound>
    private lateinit var changeSoundAdapter: SoundAdapter2
    private lateinit var soundController: SoundController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeSoundBinding=ActivityChangeSoundBinding.inflate(layoutInflater)
        setContentView(changeSoundBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.custom_sound_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        soundController=SoundController(this)
        getListSound()
        changeSoundAdapter= SoundAdapter2(soundList)
        changeSoundBinding.rcvCustomSoundLayout.layoutManager=GridLayoutManager(this,3)
        changeSoundBinding.rcvCustomSoundLayout.adapter=changeSoundAdapter
    }

    private fun getListSound() {
        soundList = listOf(
            Sound(Constant.Sound.CAT, R.drawable.cat_meowing_ic, R.drawable.bg_sound_passive),
            Sound(Constant.Sound.DOG, R.drawable.dog_barking_ic, R.drawable.bg_sound_passive),
            Sound(
                Constant.Sound.HEY_STAY_HERE,
                R.drawable.hey_stay_here_ic,
                R.drawable.bg_sound_passive
            ),
            Sound(Constant.Sound.WHISTLE, R.drawable.whistle_ic, R.drawable.bg_sound_passive),
            Sound(Constant.Sound.HELLO, R.drawable.hello_ic, R.drawable.bg_sound_passive),
            Sound(Constant.Sound.CAR_HONK, R.drawable.car_horn_ic, R.drawable.bg_sound_passive),
            Sound(Constant.Sound.DOOR_BELL, R.drawable.door_bell_ic, R.drawable.bg_sound_passive),
            Sound(Constant.Sound.PARTY_HORN, R.drawable.party_horn_ic, R.drawable.bg_sound_passive),
            Sound(
                Constant.Sound.POLICE_WHISTLE,
                R.drawable.police_whistle_ic,
                R.drawable.bg_sound_passive
            ),
            Sound(Constant.Sound.CAVARLY, R.drawable.cavalry_ic, R.drawable.bg_sound_passive),
            Sound(
                Constant.Sound.ARMY_TRUMPET,
                R.drawable.army_trumpet_ic,
                R.drawable.bg_sound_passive
            ),
            Sound(Constant.Sound.RIFLE, R.drawable.rifle_ic, R.drawable.bg_sound_passive),
        )
    }
}