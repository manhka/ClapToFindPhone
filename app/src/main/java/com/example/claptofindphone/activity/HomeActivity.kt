package com.example.claptofindphone.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.HomeAdapter
import com.example.claptofindphone.adapter.SoundAdapter
import com.example.claptofindphone.databinding.ActivityHomeBinding
import com.example.claptofindphone.databinding.DialogRateUsBinding
import com.example.claptofindphone.databinding.ExitDialogBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : BaseActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var soundAdapter: SoundAdapter
    private lateinit var soundList: List<Sound>
    private val REQUEST_CODE_POST_NOTIFICATION = 101
    private lateinit var permissionController: PermissionController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeBackPressCallBack {
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
        val timeComeToHome = SharePreferenceUtils.getTimeComeHome()
        if (timeComeToHome in 0..3) {
            SharePreferenceUtils.setTimeComeHome(SharePreferenceUtils.getTimeComeHome() + 1)
        }
        permissionController = PermissionController()

        handleNotificationPermission()
        val myService = MyService()
        myService.handleBackPress(this)
        soundList = InstallData.getListSound(this)
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

        val homeOpenFragment = SharePreferenceUtils.getOpenHomeFragment()
        if (homeOpenFragment == Constant.Service.CLAP_TO_FIND_PHONE) {
            homeViewPager.currentItem = 0
        } else if (homeOpenFragment == Constant.Service.VOICE_PASSCODE) {
            homeViewPager.currentItem = 1
        } else if (homeOpenFragment == Constant.Service.POCKET_MODE) {
            homeViewPager.currentItem = 2
        } else if (homeOpenFragment == Constant.Service.CHARGER_PHONE) {
            homeViewPager.currentItem = 3
        } else if (homeOpenFragment == Constant.Service.DONT_TOUCH_MY_PHONE) {
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

            val isRunningService = SharePreferenceUtils.getRunningService()
            if (permissionController.hasAudioPermission(this)) {
                if (permissionController.isInternetAvailable(this)) {
                    if (isRunningService != "") {
                        Toast.makeText(
                            this,
                            getString(R.string.deactive_all_feature_before_run),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(this, VoicePasscodeActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.connect_internet_to_use_this_feature,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, R.string.active_audio_to_use_this_feature, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // show rate dialog after first time use service
        if (SharePreferenceUtils.isShowRateDialog() == 1) {
            val dialogBinding = DialogRateUsBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()

            dialogBinding.yesButton.setOnClickListener {
                SharePreferenceUtils.setIsShowRateDialog(2)
                customDialog.dismiss()
            }
            dialogBinding.noButton.setOnClickListener {
                SharePreferenceUtils.setIsShowRateDialog(1)
                customDialog.dismiss()
            }
            dialogBinding.exitButton.setOnClickListener {
                customDialog.dismiss()
            }

        }
    }

    // create list of sound


    private fun checkNotificationPermission(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun getDeniedCount(): Int {
        return SharePreferenceUtils.getDeniCount()
    }

    private fun incrementDeniedCount() {
        val currentCount = getDeniedCount()
        SharePreferenceUtils.setDeniCount(currentCount + 1)
    }

    private fun handleNotificationPermission() {
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

