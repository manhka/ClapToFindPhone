package com.example.claptofindphone.fragment.home

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.DialogClapAndWhistleBinding
import com.example.claptofindphone.databinding.FragmentClapToFindInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController

class ClapToFindFragment : Fragment() {
    private lateinit var serviceSharedPreferences: SharedPreferences
    private lateinit var clapToFindInHomeBinding: FragmentClapToFindInHomeBinding
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
        clapToFindInHomeBinding =
            FragmentClapToFindInHomeBinding.inflate(inflater, container, false)
        return clapToFindInHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clapToFindInHomeBinding.handClapButton.setOnClickListener {
            if (permissionController.hasAudioPermission(requireActivity()) && permissionController.isOverlayPermissionGranted(
                    requireActivity()
                )
            ) {
                serviceSharedPreferences.edit().putString(
                    Constant.Service.RUNNING_SERVICE,
                    Constant.Service.CLAP_AND_WHISTLE_RUNNING
                ).apply()
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
                if (!isOnClapService) {
                    // check if other service running
                    if (isOnVoicePasscodeService || isOnPocketModeService || isOnDontTouchMyPhoneService || isOnChargerAlarmService) {
                        Toast.makeText(
                            requireContext(),
                            "Another service is running",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        onService(
                            Constant.Service.CLAP_TO_FIND_PHONE,
                            Constant.Service.CLAP_AND_WHISTLE_RUNNING
                        )

                    }

                } else {
                    clapToFindInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                    clapToFindInHomeBinding.handIc.visibility = View.VISIBLE
                    clapToFindInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                    serviceSharedPreferences.edit()
                        .putBoolean(Constant.Service.CLAP_TO_FIND_PHONE, false).apply()
                    AnimationUtils.applyAnimations(clapToFindInHomeBinding.handIc)
                    AnimationUtils.stopAnimations(clapToFindInHomeBinding.round3)
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
        val isOnClapService =
            serviceSharedPreferences.getBoolean(Constant.Service.CLAP_TO_FIND_PHONE, false)
        if (isOnClapService) {
            onService(
                Constant.Service.CLAP_TO_FIND_PHONE,
                Constant.Service.CLAP_AND_WHISTLE_RUNNING
            )
            AnimationUtils.stopAnimations(clapToFindInHomeBinding.handIc)
            AnimationUtils.applyWaveAnimation(clapToFindInHomeBinding.round3)

        } else {
            AnimationUtils.applyAnimations(clapToFindInHomeBinding.handIc)
            AnimationUtils.stopAnimations(clapToFindInHomeBinding.round3)
        }

        val firstTimeSharedPreferences = this.requireActivity()
            .getSharedPreferences(Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES, MODE_PRIVATE)
        val isFirstTimeGetInClap = firstTimeSharedPreferences.getBoolean(
            Constant.SharePres.FIRST_TIME_GET_IN_CLAP_AND_WHISTLE,
            true
        )
        if (isFirstTimeGetInClap) {
            val dialogBinding = DialogClapAndWhistleBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            firstTimeSharedPreferences.edit()
                .putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_CLAP_AND_WHISTLE, false).apply()
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }

    }

    private fun onService(typeOfService: String, typeOfServiceIntent: String) {
        AnimationUtils.stopAnimations(clapToFindInHomeBinding.handIc)
        AnimationUtils.applyWaveAnimation(clapToFindInHomeBinding.round3)
        clapToFindInHomeBinding.txtActionStatus.text =
            getString(R.string.tap_to_deactive)
        clapToFindInHomeBinding.handIc.visibility = View.GONE
        clapToFindInHomeBinding.round2.setImageResource(R.drawable.round2_active)
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
    }

}