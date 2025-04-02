package com.example.claptofindphone.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.SoundAdapter
import com.example.claptofindphone.databinding.ActivityHomeBinding
import com.example.claptofindphone.databinding.DialogRateUsBinding
import com.example.claptofindphone.databinding.ExitDialogBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.MyServiceNoMicro
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils
import com.google.android.material.tabs.TabLayout

class HomeActivity : BaseActivity() {

    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var soundAdapter: SoundAdapter
    private lateinit var soundList: List<Sound>
    private val REQUEST_CODE_POST_NOTIFICATION = 101
    private var mLastClickTime: Long = 0
    private var anim: ScaleAnimation? = null
    private val imgService = listOf(
        R.drawable.hand_clap,
        R.drawable.mic_ic,
        R.drawable.pocket_mode,
        R.drawable.ic_charger_alarm,
        R.drawable.touch_phone
    )
    private var currentTabIndex = SharePreferenceUtils.selectedTabIndex()
    private lateinit var permissionController: PermissionController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        setupAnimation()
        if (SharePreferenceUtils.isShowRateDialog() == 0) {
            SharePreferenceUtils.setIsShowRateDialog(1)
        }
        homeBinding.txtHomeTitle.isSelected = true

        // back click
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
        // get time come in home
        val timeComeToHome = SharePreferenceUtils.getTimeComeHome()
        if (timeComeToHome in 1..3) {
            SharePreferenceUtils.setTimeComeHome(SharePreferenceUtils.getTimeComeHome() + 1)
        }
        permissionController = PermissionController()
        handleNotificationPermission()
        // sound
        soundList = InstallData.getListSound(this)
        soundAdapter = SoundAdapter(this, soundList)
        homeBinding.rcvHomeSound.layoutManager = GridLayoutManager(this, 3)
        homeBinding.rcvHomeSound.adapter = soundAdapter
        // install viewpager and tab layout
        homeBinding.tabLayoutHome.addTab(
            homeBinding.tabLayoutHome.newTab().setText(getString(R.string.clap_to_find))
        )
        homeBinding.tabLayoutHome.addTab(
            homeBinding.tabLayoutHome.newTab().setText(getString(R.string.voice_passcode))
        )
        homeBinding.tabLayoutHome.addTab(
            homeBinding.tabLayoutHome.newTab().setText(getString(R.string.pocket_mode))
        )
        homeBinding.tabLayoutHome.addTab(
            homeBinding.tabLayoutHome.newTab().setText(getString(R.string.charger_alarm))
        )
        homeBinding.tabLayoutHome.addTab(
            homeBinding.tabLayoutHome.newTab().setText(getString(R.string.dont_touch_my_phone))
        )

        for (i in 0 until homeBinding.tabLayoutHome.tabCount) {
            val tabView = homeBinding.tabLayoutHome.getTabAt(i)?.view
            val layoutParams = tabView?.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(20, 0, 20, 0)
            tabView.layoutParams = layoutParams
            if (SharePreferenceUtils.selectedTabIndex() == i) {
                tabView.setBackgroundResource(R.drawable.bg_tab_selected)
            } else {
                tabView.setBackgroundResource(R.drawable.bg_tab_unselected)
            }
        }


        // show rate dialog after first time use service
        if (SharePreferenceUtils.isShowRateDialog() == 2) {
            val dialogBinding = DialogRateUsBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()

            dialogBinding.yesButton.setOnClickListener {
                SharePreferenceUtils.setIsShowRateDialog(3)
                customDialog.dismiss()
            }
            dialogBinding.noButton.setOnClickListener {
                SharePreferenceUtils.setIsShowRateDialog(1)
                customDialog.dismiss()
            }
            dialogBinding.exitButton.setOnClickListener {
                SharePreferenceUtils.setIsShowRateDialog(1)
                customDialog.dismiss()
            }

        }
        startDiamondAnimation(homeBinding.imgViewDiamond)
        if (SharePreferenceUtils.isShowNotyWhenComeToHome()) {
            if (checkNotificationPermission(this)) {
                SharePreferenceUtils.setIsShowNotyWhenComeToHome(false)
                val intent = Intent(this, MyServiceNoMicro::class.java)
                intent.putExtra("turnOnNotifyFromHome", true)
                startService(intent)
            }else{
                SharePreferenceUtils.setIsShowNotyWhenComeToHome(true)
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            homeBinding.tabLayoutHome.selectTab(
                homeBinding.tabLayoutHome.getTabAt(
                    SharePreferenceUtils.selectedTabIndex()
                )
            )
            homeBinding.imgViewIconService.setImageResource(imgService[SharePreferenceUtils.selectedTabIndex()])
            homeBinding.tabLayoutHome.setScrollPosition(
                SharePreferenceUtils.selectedTabIndex(),
                0f,
                true)
        }
    }
    override fun onResume() {
        super.onResume()



        // update ui after destroy and come back
        if (SharePreferenceUtils.getRunningService() == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            if (permissionController.hasAudioPermission(this) && permissionController.isOverlayPermissionGranted(
                    this
                )
            ) {
                if (SharePreferenceUtils.isNavigateFromSplash()) {
                    startMicService(SharePreferenceUtils.getRunningService())
                }
                updateUiOnService()
            } else {
                if (SharePreferenceUtils.isNavigateFromSplash()) {
                    SharePreferenceUtils.setRunningService("")
                    permissionController.showInitialDialog(
                        this,
                        Constant.Permission.BOTH_PERMISSION,
                        Constant.Service.CLAP_TO_FIND_PHONE,
                        Constant.Service.CLAP_AND_WHISTLE_RUNNING
                    )
                } else {
                    stopMicService()
                }
            }
        } else if (SharePreferenceUtils.getRunningService() == Constant.Service.VOICE_PASSCODE_RUNNING) {
            Log.d(TAG, "internet checking: ${permissionController.isInternetAvailable(this)}")
            if (permissionController.isInternetAvailable(this)) {
                if (permissionController.hasAudioPermission(this) && permissionController.isOverlayPermissionGranted(
                        this
                    )
                ) {
                    if (SharePreferenceUtils.isNavigateFromSplash()) {
                        startMicService(SharePreferenceUtils.getRunningService())
                    }
                    updateUiOnService()
                } else {
                    if (SharePreferenceUtils.isNavigateFromSplash()) {
                        SharePreferenceUtils.setRunningService("")
                        permissionController.showInitialDialog(
                            this,
                            Constant.Permission.BOTH_PERMISSION,
                            Constant.Service.VOICE_PASSCODE,
                            Constant.Service.VOICE_PASSCODE_RUNNING
                        )
                    } else {
                        stopMicService()
                    }
                }
            } else {
                Log.d(TAG, "onResume: ????")
                SharePreferenceUtils.setRunningService("")
                toastInternet()
            }
        } else if (SharePreferenceUtils.getRunningService() == Constant.Service.POCKET_MODE_RUNNING || SharePreferenceUtils.getRunningService() == Constant.Service.TOUCH_PHONE_RUNNING) {
            if (permissionController.isOverlayPermissionGranted(this)) {
                updateUiOnService()
            } else {
                stopNoMicService()
            }
        } else if (SharePreferenceUtils.getRunningService() == Constant.Service.CHARGER_ALARM_RUNNING) {
            if (permissionController.isOverlayPermissionGranted(this)) {
                if (isPlugPhone()) {
                    updateUiOnService()
                } else {
                    SharePreferenceUtils.setRunningService("")
                    Toast.makeText(this, R.string.plug_phone, Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                stopNoMicService()
            }
        }
        // click service
        homeBinding.imgViewIconService.setOnClickListener {
            if (SharePreferenceUtils.getRunningService() == "") {
                when (currentTabIndex) {
                    0 -> {
                        onService(Constant.Service.CLAP_AND_WHISTLE_RUNNING)
                    }

                    1 -> {
                        onService(Constant.Service.VOICE_PASSCODE_RUNNING)
                    }

                    2 -> {
                        onService(Constant.Service.POCKET_MODE_RUNNING)
                    }

                    3 -> {
                        onService(Constant.Service.CHARGER_ALARM_RUNNING)
                    }

                    4 -> {
                        onService(Constant.Service.TOUCH_PHONE_RUNNING)
                    }
                }
            } else {
                when (currentTabIndex) {
                    0 -> {
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
                            stopMicService()
                        } else {
                            toast()
                        }
                    }

                    1 -> {

                        if (SharePreferenceUtils.getRunningService() == Constant.Service.VOICE_PASSCODE_RUNNING) {
                            stopMicService()
                        } else {
                            toast()
                        }
                    }

                    2 -> {
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.POCKET_MODE_RUNNING) {
                            stopNoMicService()
                        } else {
                            toast()
                        }
                    }

                    3 -> {
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.CHARGER_ALARM_RUNNING) {
                            stopNoMicService()
                        } else {
                            toast()
                        }
                    }

                    4 -> {
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.TOUCH_PHONE_RUNNING) {
                            stopNoMicService()
                        } else {
                            toast()
                        }
                    }

                }
            }

        }
        homeBinding.tabLayoutHome.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab!!.position
                if (tab.text.toString() == getString(R.string.clap_to_find)) {
                    homeBinding.imgViewIconService.setImageResource(R.drawable.hand_clap)
                    homeBinding.changeAudioPasscodeButton.visibility = View.GONE
                    if (SharePreferenceUtils.getRunningService() == "" || SharePreferenceUtils.getRunningService() == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
                        SharePreferenceUtils.setSelectedTabIndex(tab.position)
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
                            updateUiOnService()
                        }
                    } else {
                        updateUiOffService()
                    }
                } else if (tab.text.toString() == getString(R.string.voice_passcode)) {
                    homeBinding.imgViewIconService.setImageResource(R.drawable.mic_ic)
                    homeBinding.changeAudioPasscodeButton.visibility = View.VISIBLE
                    if (SharePreferenceUtils.getRunningService() == "" || SharePreferenceUtils.getRunningService() == Constant.Service.VOICE_PASSCODE_RUNNING) {
                        SharePreferenceUtils.setSelectedTabIndex(tab.position)
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.VOICE_PASSCODE_RUNNING) {
                            updateUiOnService()
                        }
                    } else {
                        updateUiOffService()
                    }
                } else if (tab.text.toString() == getString(R.string.pocket_mode)) {
                    homeBinding.changeAudioPasscodeButton.visibility = View.GONE
                    homeBinding.imgViewIconService.setImageResource(R.drawable.pocket_mode)
                    if (SharePreferenceUtils.getRunningService() == "" || SharePreferenceUtils.getRunningService() == Constant.Service.POCKET_MODE_RUNNING) {
                        SharePreferenceUtils.setSelectedTabIndex(tab.position)
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.POCKET_MODE_RUNNING) {
                            updateUiOnService()
                        }
                    } else {
                        updateUiOffService()
                    }

                } else if (tab.text.toString() == getString(R.string.charger_alarm)) {
                    homeBinding.imgViewIconService.setImageResource(R.drawable.ic_charger_alarm)
                    homeBinding.changeAudioPasscodeButton.visibility = View.GONE
                    if (SharePreferenceUtils.getRunningService() == "" || SharePreferenceUtils.getRunningService() == Constant.Service.CHARGER_ALARM_RUNNING) {
                        SharePreferenceUtils.setSelectedTabIndex(tab.position)
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.CHARGER_ALARM_RUNNING) {
                            updateUiOnService()
                        }
                    } else {
                        updateUiOffService()
                    }
                } else if (tab?.text.toString() == getString(R.string.dont_touch_my_phone)) {
                    homeBinding.imgViewIconService.setImageResource(R.drawable.touch_phone)
                    homeBinding.changeAudioPasscodeButton.visibility = View.GONE
                    if (SharePreferenceUtils.getRunningService() == "" || SharePreferenceUtils.getRunningService() == Constant.Service.TOUCH_PHONE_RUNNING) {
                        SharePreferenceUtils.setSelectedTabIndex(tab.position)
                        if (SharePreferenceUtils.getRunningService() == Constant.Service.TOUCH_PHONE_RUNNING) {
                            updateUiOnService()
                        }
                    } else {
                        updateUiOffService()
                    }
                }
                tab.view.setBackgroundResource(R.drawable.bg_tab_selected)
                tab.select()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_unselected)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_selected)

            }

        })





        homeBinding.cardViewChangeTheme.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, ChangeThemeActivity::class.java)
            startActivity(intent)
        }
        homeBinding.cardViewFlashlight.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, ChangeFlashlightActivity::class.java)
            startActivity(intent)
        }
        homeBinding.cardViewVibrate.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, ChangeVibrateActivity::class.java)
            startActivity(intent)
        }
        homeBinding.cardViewHowToUse.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, HowToUseActivity::class.java)
            startActivity(intent)
        }
        homeBinding.settingButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }


        homeBinding.changeAudioPasscodeButton.setOnClickListener {
            val isRunningService = SharePreferenceUtils.getRunningService()
            if (isRunningService != "") {
                Toast.makeText(
                    this,
                    getString(R.string.deactive_all_feature_before_run),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (permissionController.isInternetAvailable(this)) {
                    if (permissionController.hasAudioPermission(this)) {
                        val intent = Intent(this, VoicePasscodeActivity::class.java)
                        startActivity(intent)
                    } else {
                        permissionController.showInitialDialog(
                            this,
                            Constant.Permission.RECORDING_PERMISSION,
                            Constant.Service.VOICE_PASSCODE,
                            "CHANGE_PASSCODE"
                        )
                    }
                } else {
                    toastInternet()
                }

            }
        }
    }

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

    private fun startDiamondAnimation(imageView: ImageView) {
        // Animation xoay 180 độ
        val rotate = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 180f).apply {
            duration = 1500 // Xoay trong 1 giây
        }

        // Animation nẩy lên
        val bounce = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -3f, 0f).apply {
            duration = 1500
            interpolator = BounceInterpolator() // Hiệu ứng nẩy
        }

        // Gộp animation
        val animatorSet = AnimatorSet().apply {
            playTogether(rotate, bounce)
            startDelay = 1000
        }

        // Lặp vô hạn
        animatorSet.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                animation.start()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        animatorSet.start()
    }

    private fun setupAnimation() {
        anim = ScaleAnimation(
            1.0f, 1.3f, 1.0f, 1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim!!.duration = 600
        anim!!.repeatCount = Animation.INFINITE
        anim!!.repeatMode = Animation.REVERSE
        homeBinding.handIc.startAnimation(anim)
    }

    private fun stopAnimation() {
        if (anim != null) anim!!.cancel()
    }

    private fun onService(runningService: String) {
        // clap and whistle
        if (runningService == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            if (permissionController.isOverlayPermissionGranted(this) && permissionController.hasAudioPermission(
                    this
                )
            ) {
                startMicService(runningService)
                updateUiOnService()
            } else {
                permissionController.showInitialDialog(
                    this,
                    Constant.Permission.BOTH_PERMISSION,
                    Constant.Service.CLAP_TO_FIND_PHONE,
                    Constant.Service.CLAP_AND_WHISTLE_RUNNING
                )
            }
        }
        // voice passcode
        if (runningService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            if (!permissionController.isInternetAvailable(this)) {
                toastInternet()
            } else {
                if (permissionController.isOverlayPermissionGranted(this) && permissionController.hasAudioPermission(
                        this
                    )
                ) {
                    // check passcode
                    if (SharePreferenceUtils.getVoicePasscode() == Constant.DEFAULT_PASSCODE) {
                        val intent = Intent(this, VoicePasscodeActivity::class.java)
                        startActivity(intent)
                    } else {
                        startMicService(runningService)
                        updateUiOnService()
                    }
                } else {
                    permissionController.showInitialDialog(
                        this,
                        Constant.Permission.BOTH_PERMISSION,
                        Constant.Service.VOICE_PASSCODE,
                        Constant.Service.VOICE_PASSCODE_RUNNING
                    )
                }
            }
        }
        // touch phone
        if (runningService == Constant.Service.TOUCH_PHONE_RUNNING) {
            if (permissionController.isOverlayPermissionGranted(this)) {
                startNoMicService(runningService)
                navigateToWait()
            } else {
                permissionController.showInitialDialog(
                    this,
                    Constant.Permission.OVERLAY_PERMISSION,
                    Constant.Service.DONT_TOUCH_MY_PHONE,
                    Constant.Service.TOUCH_PHONE_RUNNING
                )
            }
        }
        // charger phone
        if (runningService == Constant.Service.CHARGER_ALARM_RUNNING) {
            if (permissionController.isOverlayPermissionGranted(this)) {
                if (isPlugPhone()) {
                    startNoMicService(runningService)
                    navigateToWait()
                } else {
                    Toast.makeText(this, R.string.plug_phone, Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                permissionController.showInitialDialog(
                    this,
                    Constant.Permission.OVERLAY_PERMISSION,
                    Constant.Service.CHARGER_PHONE,
                    Constant.Service.CHARGER_ALARM_RUNNING
                )
            }
        }
        // pocket mode
        if (runningService == Constant.Service.POCKET_MODE_RUNNING) {
            if (permissionController.isOverlayPermissionGranted(this)) {
                startNoMicService(runningService)
                navigateToWait()
            } else {
                permissionController.showInitialDialog(
                    this,
                    Constant.Permission.OVERLAY_PERMISSION,
                    Constant.Service.POCKET_MODE,
                    Constant.Service.POCKET_MODE_RUNNING
                )
            }
        }
    }

    private fun navigateToWait() {
        val intent = Intent(this, WaitActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun updateUiOffService() {
        homeBinding.round3.setAnimation(R.raw.anim_home)
        homeBinding.round3.playAnimation()
        homeBinding.handIc.visibility = View.VISIBLE
        homeBinding.handIc.startAnimation(anim)
    }

    private fun updateUiOnService() {
        stopAnimation()
        homeBinding.handIc.visibility = View.GONE
        homeBinding.round3.setAnimation(R.raw.anim_home_active)
        homeBinding.round3.playAnimation()
    }

    private fun toast() {
        Toast.makeText(
            this,
            R.string.other_service_running,
            Toast.LENGTH_LONG
        )
            .show()
    }

    private fun toastInternet() {
        Toast.makeText(
            this,
            R.string.connect_internet_to_use_this_feature,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startMicService(service: String) {
        SharePreferenceUtils.setRunningService(service)
        val intentService = Intent(this, MyService::class.java)
        intentService.putExtra(Constant.Service.RUNNING_SERVICE, service)
        startService(intentService)
    }

    private fun stopMicService() {
        SharePreferenceUtils.setRunningService("")
        updateUiOffService()
        val intentService = Intent(this, MyService::class.java)
        stopService(intentService)
    }

    private fun startNoMicService(service: String) {
        SharePreferenceUtils.setRunningService(service)
        val intentService = Intent(this, MyServiceNoMicro::class.java)
        intentService.putExtra(Constant.Service.RUNNING_SERVICE, service)
        startService(intentService)
    }

    private fun stopNoMicService() {
        SharePreferenceUtils.setRunningService("")
        updateUiOffService()
        val intentService = Intent(this, MyServiceNoMicro::class.java)
        stopService(intentService)
    }

    private fun isPlugPhone(): Boolean {
        val batteryIntent =
            registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return (status == BatteryManager.BATTERY_STATUS_CHARGING)
    }
}


