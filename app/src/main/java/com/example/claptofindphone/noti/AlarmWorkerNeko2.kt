package com.example.claptofindphone.noti

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.claptofindphone.BuildConfig

class AlarmWorkerNeko2(val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
        companion object{
            var tag = "${BuildConfig.APPLICATION_ID}_ALARM"
        }
    override suspend fun doWork(): Result {
        Log.d("TAG", "doWork: ")
        NotificationUtilsNeko2.runNotification(appContext)
        return Result.success()
    }
}