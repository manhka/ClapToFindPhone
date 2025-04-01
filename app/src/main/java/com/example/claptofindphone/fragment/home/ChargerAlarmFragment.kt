package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.WaitActivity
import com.example.claptofindphone.databinding.DialogChargerAlarmDialogBinding
import com.example.claptofindphone.databinding.FragmentChargerAlarmInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyServiceNoMicro
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils.getRunningService
import com.example.claptofindphone.utils.SharePreferenceUtils.isNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.isOnService
import com.example.claptofindphone.utils.SharePreferenceUtils.isShowChargerPhoneDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.isWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsOnNotify
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsShowChargerPhoneDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setOpenHomeFragment
import com.example.claptofindphone.utils.SharePreferenceUtils.setRunningService

class ChargerAlarmFragment : Fragment() {
    private var binding: FragmentChargerAlarmInHomeBinding? = null
    private var permissionController: PermissionController? = null
    private var anim: ScaleAnimation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionController = PermissionController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChargerAlarmInHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnimation()
        binding!!.chargerAlarmButton.setOnClickListener { v -> handleChargerAlarmButtonClick() }
    }

    private fun onService(runningService: String) {
        stopAnimation()
        binding!!.round3.setAnimation(R.raw.anim_home_orange)
        binding!!.round3.playAnimation()
        binding!!.txtActionStatus.setText(R.string.tap_to_deactive)
        binding!!.handIc.visibility = View.GONE
        binding!!.round2.setImageResource(R.drawable.round2_active)
        if (!isWaited()) {
            setRunningService(runningService)
            setIsWaited(true)
            val intent = Intent(requireContext(), MyServiceNoMicro::class.java)
            intent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            requireContext().startService(intent)
            val waitIntent = Intent(
                requireContext(),
                WaitActivity::class.java
            )
            waitIntent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            startActivity(waitIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNavigateFromSplash()) {
            setIsNavigateFromSplash(false)
            if (checkPermission()) {
                if (isPlugPhone()) {
                    onService(Constant.Service.CHARGER_ALARM_RUNNING)
                } else {
                    Toast.makeText(requireActivity(), R.string.plug_phone, Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                if (isOnService()) {
                    stopService()
                }
                requestPermission()
            }

        } else {
            handleChargerAlarmService()
            showFirstTimeDialog()
        }

    }

    private fun handleChargerAlarmButtonClick() {
        val runningService = getRunningService()
        setOpenHomeFragment(Constant.Service.CHARGER_PHONE)
        if (runningService == "") {
            if (checkPermission()) {
                if (isPlugPhone()) {
                    onService(Constant.Service.CHARGER_ALARM_RUNNING)
                } else {
                    Toast.makeText(requireActivity(), R.string.plug_phone, Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                requestPermission()
            }
        } else if (runningService != Constant.Service.CHARGER_ALARM_RUNNING) {
            Toast.makeText(requireContext(), R.string.other_service_running, Toast.LENGTH_LONG)
                .show()
        } else {
            stopService()
        }
    }

    private fun stopService() {
        binding!!.round3.setAnimation(R.raw.anim_home)
        binding!!.round3.playAnimation()
        binding!!.txtActionStatus.setText(R.string.tap_to_active)
        binding!!.handIc.visibility = View.VISIBLE
        binding!!.round2.setImageResource(R.drawable.round2_passive)
        setRunningService("")
        binding!!.handIc.startAnimation(anim)
        setIsWaited(false)
        requireContext().stopService(Intent(requireContext(), MyServiceNoMicro::class.java))
    }

    private fun handleChargerAlarmService() {
        val isOnChargerAlarmService = getRunningService()
        if (isOnChargerAlarmService == "") {
            binding!!.handIc.startAnimation(anim)
            setOpenHomeFragment(Constant.Service.CHARGER_PHONE)
        } else if (isOnChargerAlarmService == Constant.Service.CHARGER_ALARM_RUNNING) {
            if (checkPermission()) {
                onService(Constant.Service.CHARGER_ALARM_RUNNING)
            } else {
                if (isOnService()) {
                    stopService()
                }
            }
        } else {
            binding!!.handIc.startAnimation(anim)
        }
    }

    private fun showFirstTimeDialog() {
        if (isShowChargerPhoneDialog()) {
            val dialogBinding = DialogChargerAlarmDialogBinding.inflate(
                layoutInflater
            )
            val customDialog = AlertDialog.Builder(requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()
            customDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            setIsShowChargerPhoneDialog(false)
            dialogBinding.yesButton.setOnClickListener { v -> customDialog.dismiss() }
        }
    }


    private fun setupAnimation() {
        anim = ScaleAnimation(
            1.0f, 1.3f, 1.0f, 1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim!!.duration = 600
        anim!!.repeatCount = Animation.INFINITE
        anim!!.repeatMode = Animation.REVERSE
        binding!!.handIc.startAnimation(anim)
    }

    private fun stopAnimation() {
        if (anim != null) {
            anim!!.cancel()
        }
    }


    private fun isPlugPhone(): Boolean {
        val batteryIntent =
            requireActivity().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)
    }

    private fun requestPermission() {
        permissionController!!.showInitialDialog(
            requireActivity(),
            Constant.Permission.OVERLAY_PERMISSION,
            Constant.Service.CHARGER_PHONE,
            Constant.Service.CHARGER_ALARM_RUNNING
        )
    }

    private fun checkPermission(): Boolean {
        return permissionController!!.isOverlayPermissionGranted(requireActivity())
    }
}