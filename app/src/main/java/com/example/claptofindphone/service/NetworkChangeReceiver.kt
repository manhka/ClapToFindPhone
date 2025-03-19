package com.example.claptofindphone.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val permissionController = PermissionController()

        if (context != null) {
            if (!permissionController.isInternetAvailable(context) ) {
                Toast.makeText(context, "Internet disconnected", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
