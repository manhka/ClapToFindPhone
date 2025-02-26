package com.example.claptofindphone.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.claptofindphone.R
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.noti.AlarmWorkerNeko2
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        changeBackPressCallBack {  }
        setContentView(R.layout.activity_splash)
        if (isNotificationEnabled(this)){
            showAlarmNotification()
        }
        val myService = MyService()
        myService.handleBackPress(this)
        Handler(Looper.getMainLooper()).postDelayed({
            navigate()
            finish()
        }, 2000)


    }
    private fun navigate() {
        val timeComeToHome = SharePreferenceUtils.getTimeComeHome()
        if (timeComeToHome==0) {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            finish()

        } else {
            val intent = Intent(this, InstallingLanguageActivity::class.java)
            startActivity(intent)
            finish()
        }

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
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled()
        } else {
            // Trước Android 13, không cần kiểm tra quyền này
            true
        }
    }

}