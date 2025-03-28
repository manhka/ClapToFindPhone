package com.example.claptofindphone.service

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.GrantPermissionActivity
import com.example.claptofindphone.databinding.DialogInitalRequestPermissionBinding
import com.example.claptofindphone.databinding.DialogRequestPermissionBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.utils.SharePreferenceUtils

class PermissionController {


    fun hasAudioPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestAudioPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            Constant.Permission.AUDIO_PERMISSION_REQUEST_CODE
        )
    }

    fun isOverlayPermissionGranted(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(activity)
        } else {
            return true
        }
    }

    fun requestOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivityForResult(
                intent, Constant.Permission.OVERLAY_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun showSettingsDialog(typeOfPermission: String, activity: Activity) {
        val layoutInflater = LayoutInflater.from(activity)
        val dialogBinding = DialogRequestPermissionBinding.inflate(layoutInflater)
        val customDialog = AlertDialog.Builder(activity)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        if (typeOfPermission == Constant.Permission.RECORDING_PERMISSION) {
            dialogBinding.title.text = activity.getString(R.string.record_permission)
            dialogBinding.content.text = activity.getString(R.string.record_permission_content)
        } else if (typeOfPermission == Constant.Permission.OVERLAY_PERMISSION) {
            dialogBinding.title.text = activity.getString(R.string.overlay_permission)
            dialogBinding.content.text = activity.getString(R.string.overlay_permission_sub)
        }
        customDialog.show()

        dialogBinding.settingButton.setOnClickListener {
            customDialog?.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivity(intent)
        }
        dialogBinding.cancelButton.setOnClickListener {
            customDialog.dismiss()
        }
    }


    fun showInitialDialog(
        activity: Activity,
        typeOfPermission: String,
        typeOfService: String,
        typeOfRunningService: String
    ) {
        SharePreferenceUtils.setOpenHomeFragment(typeOfService)
        val layoutInflater = LayoutInflater.from(activity)
        val dialogBinding = DialogInitalRequestPermissionBinding.inflate(layoutInflater)
        val customDialog = AlertDialog.Builder(activity)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.setOnCancelListener {
            SharePreferenceUtils.setRunningService("")
        }

        customDialog.show()

        dialogBinding.gotItButton.setOnClickListener {
            customDialog?.dismiss()
            if (!isOverlayPermissionGranted(activity) || !hasAudioPermission(activity)) {
                val intent = Intent(activity, GrantPermissionActivity::class.java)
                intent.putExtra("typeOfPermission", typeOfPermission)
                intent.putExtra("typeOfRunningService", typeOfRunningService)
                activity.startActivity(intent)
            }
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            true
        } else {
            false
        }
    }
}