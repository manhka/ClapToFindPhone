package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.WaitActivity
import com.example.claptofindphone.databinding.DialogChargerAlarmDialogBinding
import com.example.claptofindphone.databinding.DialogTouchPhoneBinding
import com.example.claptofindphone.databinding.FragmentChargerAlarmInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils
import kotlin.math.log


class ChargerAlarmFragment() : Fragment() {
    private lateinit var chargerAlarmInHomeBinding: FragmentChargerAlarmInHomeBinding
    private lateinit var permissionController: PermissionController
    private var batteryReceiver: BroadcastReceiver? = null
    private var isOnWaitActivity: Boolean = false
    private var anim: ScaleAnimation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionController = PermissionController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chargerAlarmInHomeBinding =
            FragmentChargerAlarmInHomeBinding.inflate(inflater, container, false)
        return chargerAlarmInHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnim()

        chargerAlarmInHomeBinding.chargerAlarmButton.setOnClickListener {
            var isChargerPhone = false
            // Kiểm tra ngay lập tức trạng thái pin
            val batteryIntent = requireActivity().registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> {
                    // Điện thoại đang sạc
                    isChargerPhone = true
                }

                BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                    // Rút sạc
                    isChargerPhone = false
                }
            }
            batteryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                        // Lấy trạng thái pin
                        when (status) {
                            BatteryManager.BATTERY_STATUS_CHARGING -> {
                                isChargerPhone = true
                            }

                            BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                                isChargerPhone = false
                            }
                        }
                    }
                }
            }
            // Đăng ký Receiver cho sự kiện pin
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            requireActivity().registerReceiver(batteryReceiver, intentFilter)

            // Kiểm tra quyền và các hành động tiếp theo
            if (permissionController.isOverlayPermissionGranted(requireActivity())) {
                val runningService = SharePreferenceUtils.getRunningService()
                if (runningService == "") {
                    SharePreferenceUtils.setOpenHomeFragment(Constant.Service.CHARGER_PHONE)
                    if (isChargerPhone) {
                        SharePreferenceUtils.setIsWaited(false)
                        onService(Constant.Service.CHARGER_ALARM_RUNNING)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.plug_phone),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (runningService != Constant.Service.CHARGER_ALARM_RUNNING) {
                    Toast.makeText(
                        requireContext(),
                        R.string.other_service_running,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    chargerAlarmInHomeBinding.txtActionStatus.text =
                        getString(R.string.tap_to_active)
                    chargerAlarmInHomeBinding.handIc.visibility = View.VISIBLE
                    chargerAlarmInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                    SharePreferenceUtils.setRunningService("")
                    chargerAlarmInHomeBinding.handIc.startAnimation(anim)
                    SharePreferenceUtils.setIsWaited(false)
                    val intent = Intent(requireContext(), MyService::class.java)
                    requireContext().stopService(intent)
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
        val isOnChargerAlarmService = SharePreferenceUtils.getRunningService()
        if (isOnChargerAlarmService == Constant.Service.CHARGER_ALARM_RUNNING) {
            onService(Constant.Service.CHARGER_ALARM_RUNNING)

        } else {
            chargerAlarmInHomeBinding.handIc.startAnimation(anim)
        }

        val isFirstTimeGetInChargerAlarm = SharePreferenceUtils.isShowChargerPhoneDialog()
        if (isFirstTimeGetInChargerAlarm) {
            val dialogBinding = DialogChargerAlarmDialogBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog =
                AlertDialog.Builder(this.requireActivity()).setView(dialogBinding.root)
                    .setCancelable(false).create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            SharePreferenceUtils.setIsShowChargerPhoneDialog(false)
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }

    private fun onService(runningService: String) {
        stopAnim()
        chargerAlarmInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_deactive)
        chargerAlarmInHomeBinding.handIc.visibility = View.GONE
        chargerAlarmInHomeBinding.round2.setImageResource(R.drawable.round2_active)
        SharePreferenceUtils.setRunningService(runningService)
        isOnWaitActivity = SharePreferenceUtils.isWaited()
        if (!isOnWaitActivity) {
            val intent = Intent(requireContext(), WaitActivity::class.java)
            intent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            startActivity(intent)
            SharePreferenceUtils.setIsWaited(true)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (batteryReceiver != null) {
            requireActivity().unregisterReceiver(batteryReceiver)
        }
    }

    private fun setupAnim() {
        anim = ScaleAnimation(
            1.0f,
            1.3f,
            1.0f,
            1.3f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        anim?.duration = 600
        anim?.repeatCount = 10000
        anim?.repeatMode = Animation.REVERSE
    }
    private fun stopAnim() {
        anim?.cancel()
    }
}