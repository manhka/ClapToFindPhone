package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.DialogPocketModeBinding
import com.example.claptofindphone.databinding.FragmentPocketModeInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController

class PocketModeFragment : Fragment() {
private lateinit var pocketModeInHomeBinding: FragmentPocketModeInHomeBinding
    private lateinit var serviceSharedPreferences: SharedPreferences
    private lateinit var permissionController: PermissionController
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
        pocketModeInHomeBinding=FragmentPocketModeInHomeBinding.inflate(inflater,container,false)
        return pocketModeInHomeBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pocketModeInHomeBinding.pocketModeButton.setOnClickListener {
            if (permissionController.hasAudioPermission(requireActivity()) && permissionController.isOverlayPermissionGranted(
                    requireActivity()
                )
            ) {
                serviceSharedPreferences.edit().putString(Constant.Service.RUNNING_SERVICE,Constant.Service.POCKET_MODE_RUNNING).apply()
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
                if (!isOnPocketModeService) {
                    // check if other service running
                    if (isOnVoicePasscodeService || isOnClapService || isOnDontTouchMyPhoneService || isOnChargerAlarmService) {
                        Toast.makeText(
                            requireContext(),
                            "Another service is running",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        onService(
                            Constant.Service.POCKET_MODE,
                            Constant.Service.POCKET_MODE_RUNNING
                        )

                    }

                } else {
                    pocketModeInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                    pocketModeInHomeBinding.handIc.visibility = View.VISIBLE
                    pocketModeInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                    serviceSharedPreferences.edit()
                        .putBoolean(Constant.Service.POCKET_MODE, false).apply()
                    val intent = Intent(requireContext(), MyService::class.java)
                    requireContext().stopService(intent)
                }
            } else {
                permissionController.showInitialDialog(
                    requireActivity(),
                    Constant.Permission.BOTH_PERMISSION
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val isOnPocketModeService =
            serviceSharedPreferences.getBoolean(Constant.Service.POCKET_MODE, false)
        if (isOnPocketModeService) {
            onService(
                Constant.Service.POCKET_MODE,
                Constant.Service.POCKET_MODE_RUNNING
            )
        }

        val firstTimeSharedPreferences= this.requireActivity().getSharedPreferences(
            Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES,
            MODE_PRIVATE
        )
        val isFirstTimeGetInPocketMode=firstTimeSharedPreferences.getBoolean(Constant.SharePres.FIRST_TIME_GET_IN_POCKET_MODE,true)
        if (isFirstTimeGetInPocketMode){
            val dialogBinding = DialogPocketModeBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            firstTimeSharedPreferences.edit().putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_POCKET_MODE,false).apply()
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }
    private fun onService(typeOfService: String, typeOfServiceIntent: String) {
        pocketModeInHomeBinding.txtActionStatus.text =
            getString(R.string.tap_to_deactive)
        pocketModeInHomeBinding.handIc.visibility = View.GONE
        pocketModeInHomeBinding.round2.setImageResource(R.drawable.round2_active)
        serviceSharedPreferences.edit()
            .putBoolean(typeOfService, true).apply()
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra(
            Constant.Service.RUNNING_SERVICE,
            typeOfServiceIntent
        )
        requireContext().startService(intent)
    }
}