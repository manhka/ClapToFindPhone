package com.example.claptofindphone.noti

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.claptofindphone.activity.buildMinVersionT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit


class TimeChangeReceiverNeko2 : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(buildMinVersionT()){
            if(context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                showAlarmNotification(context)
            }
        }else{
            showAlarmNotification(context)
        }


    }
    private fun showAlarmNotification(context:Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(AlarmWorkerNeko2.tag)
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorkerNeko2>().setInitialDelay(
            getTimeDelay(), TimeUnit.MILLISECONDS
        ).addTag(AlarmWorkerNeko2.tag).build()
        workManager.enqueue(workRequest)

        CoroutineScope(Dispatchers.Default).launch {
            WorkManager.getInstance(context).getWorkInfosByTagFlow(AlarmWorkerNeko2.tag).collect{ workInfo ->
                if(workInfo != null && workInfo.isNotEmpty()){
                    if (workInfo[0].state == WorkInfo.State.SUCCEEDED) {
                        workManager.cancelAllWorkByTag(AlarmWorkerNeko2.tag)
                        val workRequest1 = OneTimeWorkRequestBuilder<AlarmWorkerNeko2>().setInitialDelay(
                            getTimeDelay(), TimeUnit.MILLISECONDS
                        ).addTag(AlarmWorkerNeko2.tag).build()
                        workManager.enqueue(workRequest1)
                    }
                    if (workInfo[0].state == WorkInfo.State.FAILED) {
                        workManager.cancelAllWorkByTag(AlarmWorkerNeko2.tag)
                        val workRequest2 = OneTimeWorkRequestBuilder<AlarmWorkerNeko2>().setInitialDelay(
                            getTimeDelay(), TimeUnit.MILLISECONDS
                        ).addTag(AlarmWorkerNeko2.tag).build()
                        workManager.enqueue(workRequest2)
                    }
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
            this[Calendar.HOUR_OF_DAY] = 7
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

}
