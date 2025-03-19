package com.example.claptofindphone

import android.app.Application
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import com.example.claptofindphone.service.NetworkChangeReceiver
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class MyApplication : Application(){
    private lateinit var networkReceiver: NetworkChangeReceiver
    override fun onCreate() {
        super.onCreate()
        SharePreferenceUtils.init(this)
        networkReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(networkReceiver)

    }
}