package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.registerReceiver
import com.example.claptofindphone.R

import com.example.claptofindphone.databinding.DialogChargerAlarmDialogBinding
import com.example.claptofindphone.databinding.FragmentChargerAlarmInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController


class ChargerAlarmFragment : Fragment() {
    private lateinit var chargerAlarmInHomeBinding: FragmentChargerAlarmInHomeBinding
    private lateinit var serviceSharedPreferences: SharedPreferences
    private lateinit var permissionController: PermissionController
    private var batteryReceiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceSharedPreferences = requireActivity().getSharedPreferences(
            Constant.SharePres.SERVICE_SHARE_PRES,
            MODE_PRIVATE
        )
        permissionController = PermissionController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chargerAlarmInHomeBinding =
            FragmentChargerAlarmInHomeBinding.inflate(inflater, container, false)
        return chargerAlarmInHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        chargerAlarmInHomeBinding.chargerAlarmButton.setOnClickListener {
            serviceSharedPreferences.edit()
                .putString(Constant.Service.RUNNING_SERVICE, Constant.Service.CHARGER_ALARM_RUNNING)
                .apply()
            if (permissionController.isOverlayPermissionGranted(requireActivity())) {
                val isOnVoicePasscodeService =
                    serviceSharedPreferences.getBoolean(Constant.Service.VOICE_PASSCODE, false)
                val isOnDontTouchMyPhoneService =
                    serviceSharedPreferences.getBoolean(Constant.Service.DONT_TOUCH_MY_PHONE, false)
                val isOnPocketModeService =
                    serviceSharedPreferences.getBoolean(Constant.Service.POCKET_MODE, false)
                val isOnChargerAlarmService =
                    serviceSharedPreferences.getBoolean(Constant.Service.CHARGER_PHONE, false)
                val isOnClapService =
                    serviceSharedPreferences.getBoolean(Constant.Service.CLAP_TO_FIND_PHONE, false)
                if (!isOnChargerAlarmService) {
                    // check if other service running
                    if (isOnVoicePasscodeService || isOnPocketModeService || isOnDontTouchMyPhoneService || isOnClapService) {
                        Toast.makeText(
                            requireContext(),
                            "Another service is running",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var isChargerPhone=false
                        batteryReceiver = object : BroadcastReceiver() {
                            override fun onReceive(context: Context?, intent: Intent?) {
                                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                                    val status =
                                        intent.getIntExtra(
                                            BatteryManager.EXTRA_STATUS,
                                            -1
                                        ) // Lấy trạng thái pin
                                    when (status) {
                                        BatteryManager.BATTERY_STATUS_CHARGING -> {
                                            // Điện thoại đang sạc
                                            isChargerPhone=true
                                            onService(
                                                Constant.Service.CHARGER_PHONE,
                                                Constant.Service.CHARGER_ALARM_RUNNING
                                            )
                                        }

                                        BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                                            // Rút sạc
                                            if (isChargerPhone){
                                                isChargerPhone=false
                                                requireActivity().unregisterReceiver(batteryReceiver)
                                            }else{
                                                Toast.makeText(
                                                    getContext(),
                                                    getString(R.string.plug_phone),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Đăng ký Receiver cho sự kiện pin
                        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                        requireActivity().registerReceiver(batteryReceiver, intentFilter)

                    }

                } else {
                    chargerAlarmInHomeBinding.txtActionStatus.text =
                        getString(R.string.tap_to_active)
                    chargerAlarmInHomeBinding.handIc.visibility = View.VISIBLE
                    chargerAlarmInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                    serviceSharedPreferences.edit()
                        .putBoolean(Constant.Service.CHARGER_PHONE, false).apply()
                    val intent = Intent(requireContext(), MyService::class.java)
                    requireContext().stopService(intent)
                    if (batteryReceiver!=null){
                        requireActivity().unregisterReceiver(batteryReceiver)
                    }
                }
            } else {
                permissionController.showInitialDialog(
                    requireActivity(),
                    Constant.Permission.OVERLAY_PERMISSION
                )
            }


        }
    }

    override fun onResume() {
        super.onResume()

        val isOnChargerAlarmService =
            serviceSharedPreferences.getBoolean(Constant.Service.CHARGER_PHONE, false)
        if (isOnChargerAlarmService) {
            onService(
                Constant.Service.CHARGER_PHONE,
                Constant.Service.CHARGER_ALARM_RUNNING
            )
        }

        val firstTimeSharedPreferences = this.requireActivity().getSharedPreferences(
            Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES,
            MODE_PRIVATE
        )
        val isFirstTimeGetInVoicePasscode = firstTimeSharedPreferences.getBoolean(
            Constant.SharePres.FIRST_TIME_GET_IN_CHARGER_ALARM,
            true
        )
        if (isFirstTimeGetInVoicePasscode) {
            val dialogBinding = DialogChargerAlarmDialogBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            firstTimeSharedPreferences.edit()
                .putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_CHARGER_ALARM, false).apply()
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }

    private fun onService(typeOfService: String, typeOfServiceIntent: String) {
        chargerAlarmInHomeBinding.txtActionStatus.text =
            getString(R.string.tap_to_deactive)
        chargerAlarmInHomeBinding.handIc.visibility = View.GONE
        chargerAlarmInHomeBinding.round2.setImageResource(R.drawable.round2_active)
        serviceSharedPreferences.edit()
            .putBoolean(typeOfService, true).apply()
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra(
            Constant.Service.RUNNING_SERVICE,
            typeOfServiceIntent
        )
        requireContext().startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (batteryReceiver!=null){
            requireActivity().unregisterReceiver(batteryReceiver)
        }
    }
}