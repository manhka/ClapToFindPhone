package com.example.claptofindphone.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.claptofindphone.R
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.noti.AlarmWorkerNeko2
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.MyServiceNoMicro
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
private var isVoicePasscode=false
    private val permissionController=PermissionController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeBackPressCallBack { }
        setContentView(R.layout.activity_splash)
        if (isNotificationEnabled(this)) {
            showAlarmNotification()
        }
        val modeFromNoty = intent.getStringExtra("mode")
        var navigateFromOffNoty = false
        when (modeFromNoty) {
            Constant.Service.CLAP_TO_FIND_PHONE -> {
                SharePreferenceUtils.setIsNavigateFromSplash(true)
                navigateFromOffNoty = true
                SharePreferenceUtils.setRunningService(Constant.Service.CLAP_AND_WHISTLE_RUNNING)
                SharePreferenceUtils.setOpenHomeFragment(Constant.Service.CLAP_TO_FIND_PHONE)
            }

            Constant.Service.VOICE_PASSCODE -> {
                isVoicePasscode=true
                SharePreferenceUtils.setIsNavigateFromSplash(true)
                navigateFromOffNoty = true
                if (SharePreferenceUtils.getVoicePasscode() != Constant.DEFAULT_PASSCODE) {
                    SharePreferenceUtils.setRunningService(Constant.Service.VOICE_PASSCODE_RUNNING)
                    SharePreferenceUtils.setIsNavigateToChangePasscode(false)
                } else {
                    SharePreferenceUtils.setRunningService("")
                    SharePreferenceUtils.setIsNavigateToChangePasscode(true)
                }
                SharePreferenceUtils.setOpenHomeFragment(Constant.Service.VOICE_PASSCODE)
            }

            Constant.Service.POCKET_MODE -> {
                SharePreferenceUtils.setIsNavigateFromSplash(true)
                navigateFromOffNoty = true
                SharePreferenceUtils.setRunningService(Constant.Service.POCKET_MODE_RUNNING)
                SharePreferenceUtils.setOpenHomeFragment(Constant.Service.POCKET_MODE)
            }

            Constant.Service.DONT_TOUCH_MY_PHONE -> {
                SharePreferenceUtils.setIsNavigateFromSplash(true)
                navigateFromOffNoty = true
                SharePreferenceUtils.setRunningService(Constant.Service.TOUCH_PHONE_RUNNING)
                SharePreferenceUtils.setOpenHomeFragment(Constant.Service.DONT_TOUCH_MY_PHONE)
            }

            Constant.Service.CHARGER_PHONE -> {
                SharePreferenceUtils.setIsNavigateFromSplash(true)
                navigateFromOffNoty = true
                SharePreferenceUtils.setRunningService(Constant.Service.CHARGER_ALARM_RUNNING)
                SharePreferenceUtils.setOpenHomeFragment(Constant.Service.CHARGER_PHONE)
            }
        }
        val action = intent.action
        if (action == null) {
            Handler(Looper.getMainLooper()).postDelayed({
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        if (navigateFromOffNoty) {
                            if (SharePreferenceUtils.isNavigateToChangePasscode() && isVoicePasscode) {
                                if (permissionController.isInternetAvailable(this@SplashActivity) && checkPermission()) {
                                    navigateToChangePasscode()
                                } else {
                                    navigateToHome()
                                }
                            } else {
                                if (modeFromNoty==Constant.Service.DONT_TOUCH_MY_PHONE || modeFromNoty==Constant.Service.POCKET_MODE || modeFromNoty==Constant.Service.CHARGER_PHONE){
                                    if (permissionController.isOverlayPermissionGranted(this@SplashActivity)){
                                        navigateToWait()
                                    }else{
                                        navigateToHome()
                                    }
                                }else{
                                    navigateToHome()
                                }

                            }
                        } else {
                            navigate()
                        }

                        // Nếu bạn chỉ muốn chạy một lần duy nhất khi RESUMED lần đầu, có thể thêm break hoặc return ở đây
                        // return@repeatOnLifecycle
                    }
                }

            }, 2000)
        } else if (action.toBoolean()) {
                val intent = Intent(this, FoundPhoneActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                if (SharePreferenceUtils.getRunningService()!=Constant.Service.CLAP_AND_WHISTLE_RUNNING && SharePreferenceUtils.getRunningService()!=Constant.Service.VOICE_PASSCODE_RUNNING ){
                    val intentService=Intent(this,MyServiceNoMicro::class.java)
                    stopService(intentService)
                }else{
                    val intentService=Intent(this,MyService::class.java)
                    stopService(intentService)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    lifecycleScope.launchWhenResumed {
                       navigateToIntro()
                    }
                }, 2000)

            }
    }

    private fun navigateToWait() {
        SharePreferenceUtils.setIsWaited(true)
        val intentService = Intent(this, MyServiceNoMicro::class.java)
        intentService.putExtra(Constant.Service.RUNNING_SERVICE, SharePreferenceUtils.getRunningService())
        startService(intentService)
        val intentToHome = Intent(this, WaitActivity::class.java)
        startActivity(intentToHome)
        finishAffinity()
    }

    private fun navigate() {
        val timeComeToHome = SharePreferenceUtils.getTimeComeHome()
        if (timeComeToHome == 0) {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            SharePreferenceUtils.setIsFoundPhone(false)
            val intent = Intent(this, InstallingLanguageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToHome() {
        val intentToHome = Intent(this, HomeActivity::class.java)
        startActivity(intentToHome)
        finishAffinity()
    }
    private fun navigateToChangePasscode() {
        val intent = Intent(this, VoicePasscodeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
    private fun navigateToIntro() {
        val intentToHome = Intent(this, IntroductionActivity::class.java)
        startActivity(intentToHome)
        finishAffinity()
    }

    private fun showAlarmNotification() {
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWorkByTag(AlarmWorkerNeko2.tag)
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorkerNeko2>().setInitialDelay(
            getTimeDelay(), TimeUnit.MILLISECONDS
        ).addTag(AlarmWorkerNeko2.tag).build()
        workManager.enqueue(workRequest)

        WorkManager.getInstance(this).getWorkInfosByTagLiveData(AlarmWorkerNeko2.tag)
            .observe(this) { workInfo ->
                if (workInfo != null && workInfo.isNotEmpty()) {
                    if (workInfo[0].state == WorkInfo.State.SUCCEEDED) {
                        workManager.cancelAllWorkByTag(AlarmWorkerNeko2.tag)
                        val workRequest1 =
                            OneTimeWorkRequestBuilder<AlarmWorkerNeko2>().setInitialDelay(
                                getTimeDelay(), TimeUnit.MILLISECONDS
                            ).addTag(AlarmWorkerNeko2.tag).build()
                        workManager.enqueue(workRequest1)
                    }
                    if (workInfo[0].state == WorkInfo.State.FAILED) {
                        workManager.cancelAllWorkByTag(AlarmWorkerNeko2.tag)
                        val workRequest2 =
                            OneTimeWorkRequestBuilder<AlarmWorkerNeko2>().setInitialDelay(
                                getTimeDelay(), TimeUnit.MILLISECONDS
                            ).addTag(AlarmWorkerNeko2.tag).build()
                        workManager.enqueue(workRequest2)
                    }
                }


            }
    }

    private fun getTimeDelay(): Long {
        val biggerCalendar = Calendar.getInstance().apply {
            this[Calendar.HOUR_OF_DAY] = 19
            this[Calendar.MILLISECOND] = 0
            this[Calendar.MINUTE] = 0
            this[Calendar.SECOND] = 0
        }
        val smallerCalendar = Calendar.getInstance().apply {
            this[Calendar.HOUR_OF_DAY] = 19
            this[Calendar.MILLISECOND] = 0
            this[Calendar.MINUTE] = 0
            this[Calendar.SECOND] = 0
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime < smallerCalendar.timeInMillis) {
            return smallerCalendar.timeInMillis - currentTime
        }
        if (currentTime < biggerCalendar.timeInMillis) {
            return biggerCalendar.timeInMillis - currentTime
        }
        return smallerCalendar.timeInMillis + TimeUnit.DAYS.toMillis(1) - currentTime
    }

    private fun isNotificationEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Kiểm tra quyền thông báo trên Android 13 (API 33) và các phiên bản mới hơn
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled()
        } else {
            // Trước Android 13, không cần kiểm tra quyền này
            true
        }
    }
    private fun checkPermission(): Boolean {
        return permissionController.hasAudioPermission(this) &&
                permissionController.isOverlayPermissionGranted(this)
    }
}