package com.example.claptofindphone.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
import com.example.claptofindphone.noti.NotificationScheduler
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.typeOf

class HomeActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var soundAdapter: SoundAdapter
    private lateinit var soundList: List<Sound>
    private lateinit var notificationSharedPreferences: SharedPreferences
    private val REQUEST_CODE_POST_NOTIFICATION = 101
    private lateinit var permissionController: PermissionController
    private lateinit var serviceSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.POST_NOTIFICATIONS"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationScheduler().scheduleDailyNotifications(this)
        }

        permissionController=PermissionController()
        notificationSharedPreferences = getSharedPreferences(
            Constant.SharePres.NOTIFICATION_SHARE_PRES,
            MODE_PRIVATE
        )
        handleNotificationPermission()
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
        serviceSharedPreferences = getSharedPreferences(
            Constant.SharePres.SERVICE_SHARE_PRES,
            MODE_PRIVATE
        )
        val typeOfService = serviceSharedPreferences.getString(
            Constant.Service.RUNNING_SERVICE,
           null
        )
        if (typeOfService == Constant.Service.CLAP_AND_WHISTLE_RUNNING || typeOfService==null) {
            homeViewPager.currentItem = 0
        } else if (typeOfService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            homeViewPager.currentItem = 1
        } else if (typeOfService == Constant.Service.POCKET_MODE_RUNNING) {
            homeViewPager.currentItem = 2
        } else if (typeOfService == Constant.Service.CHARGER_ALARM_RUNNING) {
            homeViewPager.currentItem = 3
        } else if (typeOfService == Constant.Service.TOUCH_PHONE_RUNNING) {
            homeViewPager.currentItem = 4
        }

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


        homeBinding.changeAudioPasscodeButton.setOnClickListener {
            if (permissionController.hasAudioPermission(this)){
                if(permissionController.isInternetAvailable(this)){
                    val isOnVoicePasscodeService =
                        serviceSharedPreferences.getBoolean(Constant.Service.VOICE_PASSCODE, false)
                    val isOnDontTouchMyPhoneService =
                        serviceSharedPreferences.getBoolean(Constant.Service.DONT_TOUCH_MY_PHONE, false)
                    val isOnPocketModeService =
                        serviceSharedPreferences.getBoolean(Constant.Service.POCKET_MODE, false)
                    val isOnChargerAlarmService =
                        serviceSharedPreferences.getBoolean(Constant.Service.CHARGER_PHONE, false)
                    val isOnClapService =
                        serviceSharedPreferences.getBoolean(Constant.Service.CLAP_TO_FIND_PHONE, false)
                    if (isOnVoicePasscodeService || isOnDontTouchMyPhoneService || isOnPocketModeService || isOnChargerAlarmService || isOnClapService){
                        Toast.makeText(this,getString(R.string.deactive_all_feature_before_run),Toast.LENGTH_SHORT).show()
                    }else{
                        val intent = Intent(this, VoicePasscodeActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    Toast.makeText(this,R.string.connect_internet_to_use_this_feature,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,R.string.active_audio_to_use_this_feature,Toast.LENGTH_SHORT).show()
            }

        }
    }

    // create list of sound
    fun getListSound() {
        soundList = listOf(
            Sound(
                getString(Constant.Sound.CAT),
                R.drawable.cat_meowing_ic,
                R.drawable.bg_sound_passive,
                R.raw.cat_meowing
            ),
            Sound(
                getString(Constant.Sound.DOG),
                R.drawable.dog_barking_ic,
                R.drawable.bg_sound_passive,
                R.raw.dog_barking
            ),
            Sound(
                getString(Constant.Sound.HEY_STAY_HERE),
                R.drawable.hey_stay_here_ic,
                R.drawable.bg_sound_passive, R.raw.stay_here
            ),
            Sound(
                getString(Constant.Sound.WHISTLE),
                R.drawable.whistle_ic,
                R.drawable.bg_sound_passive,
                R.raw.whistle
            ),
            Sound(
                getString(Constant.Sound.HELLO),
                R.drawable.hello_ic,
                R.drawable.bg_sound_passive,
                R.raw.hello
            ),
            Sound(
                getString(Constant.Sound.CAR_HONK),
                R.drawable.car_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.car_honk
            ),
            Sound(
                getString(Constant.Sound.DOOR_BELL),
                R.drawable.door_bell_ic,
                R.drawable.bg_sound_passive,
                R.raw.door_bell
            ),
            Sound(
                getString(Constant.Sound.PARTY_HORN),
                R.drawable.party_horn_ic,
                R.drawable.bg_sound_passive,
                R.raw.party_horn
            ),
            Sound(
                getString(Constant.Sound.POLICE_WHISTLE),
                R.drawable.police_whistle_ic,
                R.drawable.bg_sound_passive, R.raw.police_whistle
            ),
            Sound(
                getString(Constant.Sound.CAVALRY),
                R.drawable.cavalry_ic,
                R.drawable.bg_sound_passive,
                R.raw.cavalry
            ),
            Sound(
                getString(Constant.Sound.ARMY_TRUMPET),
                R.drawable.army_trumpet_ic,
                R.drawable.bg_sound_passive, R.raw.army_trumpet
            ),
            Sound(
                getString(Constant.Sound.RIFLE),
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

    private fun checkNotificationPermission(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun getDeniedCount(): Int {
        return notificationSharedPreferences.getInt(Constant.SharePres.DENY_COUNT, 0)
    }

    fun incrementDeniedCount() {
        val currentCount = getDeniedCount()
        notificationSharedPreferences.edit().putInt(Constant.SharePres.DENY_COUNT, currentCount + 1)
            .apply()
    }

    fun handleNotificationPermission() {
        if (!checkNotificationPermission(this)) {
            val deniedCount = getDeniedCount()
            if (deniedCount < 2) {
                // Check and request notification permission for Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+ cần quyền POST_NOTIFICATIONS
                    if (ContextCompat.checkSelfPermission(
                            this,
                            "android.permission.POST_NOTIFICATIONS"
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf("android.permission.POST_NOTIFICATIONS"),
                            REQUEST_CODE_POST_NOTIFICATION
                        )
                    }
                }
            }
            incrementDeniedCount()
        }
    }

    override fun onResume() {
        super.onResume()

    }
}

