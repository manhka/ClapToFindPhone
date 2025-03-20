package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.createContext
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class NetworkChangeReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val permissionController = PermissionController()
        if (context != null) {
            if (!permissionController.isInternetAvailable(context) ) {
                Toast.makeText(context,context.createContext( Locale(SharePreferenceUtils.getLanguageCode())).getString(R.string.internet_disconnected), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, context.createContext( Locale(SharePreferenceUtils.getLanguageCode())).getString(R.string.internet_connected), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
