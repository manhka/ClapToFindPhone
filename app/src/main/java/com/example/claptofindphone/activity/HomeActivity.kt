package com.example.claptofindphone.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.HomeAdapter
import com.example.claptofindphone.adapter.SoundAdapter
import com.example.claptofindphone.databinding.ActivityHomeBinding
import com.example.claptofindphone.databinding.DialogChargerAlarmDialogBinding
import com.example.claptofindphone.databinding.DialogEditThemeBinding
import com.example.claptofindphone.databinding.ExitDialogBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.service.MyService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.typeOf

class HomeActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var soundAdapter: SoundAdapter
    private lateinit var soundList: List<Sound>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val myService = MyService()
        myService.handleBackPress(this)
        getListSound()
        soundAdapter = SoundAdapter(this, soundList)
        homeBinding.rcvHomeSound.layoutManager = GridLayoutManager(this, 3)
        homeBinding.rcvHomeSound.adapter = soundAdapter
        // install viewpager and tab layout
        val homeViewPager: ViewPager2 = homeBinding.viewPagerHome
        val homeTabLayout: TabLayout = homeBinding.tabLayoutHome
        homeViewPager.adapter = HomeAdapter(this)
        TabLayoutMediator(homeTabLayout, homeViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.clap_to_find)
                1 -> getString(R.string.voice_passcode)
                2 -> getString(R.string.pocket_mode)
                3 -> getString(R.string.charger_alarm)
                4 -> getString(R.string.dont_touch_my_phone)
                else -> ""
            }
        }.attach()

        for (i in 1 until homeTabLayout.tabCount - 1) {
            val tabView = homeTabLayout.getTabAt(i)?.view
            tabView?.setBackgroundResource(R.drawable.bg_tab_unselected)
            val layoutParams = tabView?.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(20, 0, 20, 0)
            tabView.layoutParams = layoutParams
        }
        homeTabLayout.getTabAt(0)?.view?.setBackgroundResource(R.drawable.bg_tab_selected)
        homeTabLayout.getTabAt(4)?.view?.setBackgroundResource(R.drawable.bg_tab_unselected)
        // set background for tab layout
        homeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text.toString() == getString(R.string.voice_passcode)) {
                    homeBinding.changeAudioPasscodeButton.visibility = View.VISIBLE
                } else {
                    homeBinding.changeAudioPasscodeButton.visibility = View.GONE
                }
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_selected)
                tab?.select()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_unselected)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_selected)

            }

        })
        homeBinding.cardViewChangeTheme.setOnClickListener {
            val intent = Intent(this, ChangeThemeActivity::class.java)
            startActivity(intent)
        }
        homeBinding.cardViewFlashlight.setOnClickListener {
            val intent = Intent(this, ChangeFlashlightActivity::class.java)
            startActivity(intent)
        }
        homeBinding.cardViewVibrate.setOnClickListener {
            val intent = Intent(this, ChangeVibrateActivity::class.java)
            startActivity(intent)
        }
        homeBinding.cardViewHowToUse.setOnClickListener {
            val intent = Intent(this, HowToUseActivity::class.java)
            startActivity(intent)
        }
        homeBinding.settingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

    }

    // create list of sound
    fun getListSound() {
        soundList = listOf(
            Sound(
                Constant.Sound.CAT,
                R.drawable.cat_meowing_ic,
                R.drawable.bg_sound_passive,
                R.raw.cat_meowing
            ),
            Sound(
                Constant.Sound.DOG,
                R.drawable.dog_barking_ic,
                R.drawable.bg_sound_passive,
                R.raw.dog_barking
            ),
            Sound(
                Constant.Sound.HEY_STAY_HERE,
                R.drawable.hey_stay_here_ic,
                R.drawable.bg_sound_passive, R.raw.stay_here
            ),
            Sound(
                Constant.Sound.WHISTLE,
                R.drawable.whistle_ic,
                R.drawable.bg_sound_passive,
                R.raw.whistle
            ),
            Sound(
                Constant.Sound.HELLO,
                R.drawable.hello_ic,
                R.drawable.bg_sound_passive,
                R.raw.hello
            ),
            Sound(
                Constant.Sound.CAR_HONK,
                R.drawable.car_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.car_honk
            ),
            Sound(
                Constant.Sound.DOOR_BELL,
                R.drawable.door_bell_ic,
                R.drawable.bg_sound_passive,
                R.raw.door_bell
            ),
            Sound(
                Constant.Sound.PARTY_HORN,
                R.drawable.party_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.party_horn
            ),
            Sound(
                Constant.Sound.POLICE_WHISTLE,
                R.drawable.police_whistle_ic,
                R.drawable.bg_sound_passive, R.raw.police_whistle
            ),
            Sound(
                Constant.Sound.CAVARLY,
                R.drawable.cavalry_ic,
                R.drawable.bg_sound_passive,
                R.raw.cavalry
            ),
            Sound(
                Constant.Sound.ARMY_TRUMPET,
                R.drawable.army_trumpet_ic,
                R.drawable.bg_sound_passive, R.raw.army_trumpet
            ),
            Sound(
                Constant.Sound.RIFLE,
                R.drawable.rifle_ic,
                R.drawable.bg_sound_passive,
                R.raw.rifle
            ),
        )
    }

    override fun onBackPressed() {
        val dialogBinding = ExitDialogBinding.inflate(layoutInflater)
        // Create an AlertDialog with the inflated ViewBinding root
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // Show the dialog
        customDialog.show()
        dialogBinding.cancelButton.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.exitButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finishAffinity()
        }
    }
}

