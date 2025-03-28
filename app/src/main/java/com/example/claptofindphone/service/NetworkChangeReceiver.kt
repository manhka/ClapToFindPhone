package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.createContext
import com.example.claptofindphone.fragment.home.VoicePasscodeFragment
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class NetworkChangeReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val permissionController = PermissionController()
        Handler(Looper.getMainLooper()).postDelayed(
            {if (context != null) {
            if (!permissionController.isInternetAvailable(context)) {

                Toast.makeText(context,
                    context.createContext(Locale(SharePreferenceUtils.getLanguageCode()))
                        .getString(R.string.internet_disconnected),
                    Toast.LENGTH_SHORT
                ).show()
                if (SharePreferenceUtils.getRunningService()==Constant.Service.VOICE_PASSCODE_RUNNING){
                    val intentService = Intent(context, MyService::class.java)
                    context.stopService(intentService)
                }
            } else {
//                Toast.makeText(context, context.createContext( Locale(SharePreferenceUtils.getLanguageCode())).getString(R.string.internet_connected), Toast.LENGTH_SHORT).show()
            }
        }},1000)

    }
}