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
import com.example.claptofindphone.databinding.DialogVoicePasscodeBinding
import com.example.claptofindphone.databinding.FragmentVoicePasscodeInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService


class VoicePasscodeFragment : Fragment() {
    private lateinit var serviceSharedPreferences: SharedPreferences
    private lateinit var voicePasscodeInHomeBinding: FragmentVoicePasscodeInHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceSharedPreferences = requireActivity().getSharedPreferences(
            Constant.SharePres.SERVICE_SHARE_PRES,
            MODE_PRIVATE
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        voicePasscodeInHomeBinding =
            FragmentVoicePasscodeInHomeBinding.inflate(inflater, container, false)
        return voicePasscodeInHomeBinding.root
    }

    override fun onResume() {
        super.onResume()
        val firstTimeSharedPreferences= this.requireActivity().getSharedPreferences(
            Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES,
            MODE_PRIVATE
        )
        val isFirstTimeGetInVoicePasscode=firstTimeSharedPreferences.getBoolean(Constant.SharePres.FIRST_TIME_GET_IN_VOICE_PASSCODE,true)
        if (isFirstTimeGetInVoicePasscode){
            val dialogBinding = DialogVoicePasscodeBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            firstTimeSharedPreferences.edit().putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_VOICE_PASSCODE,false).apply()
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        voicePasscodeInHomeBinding.voicePasscodeButton.setOnClickListener {
            serviceSharedPreferences.edit()
                .putString(Constant.Service.RUNNING_SERVICE, Constant.Service.VOICE_PASSCODE_RUNNING)
                .apply()
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
            if (!isOnVoicePasscodeService) {
                // check if other service running
                if (isOnClapService || isOnPocketModeService || isOnDontTouchMyPhoneService || isOnChargerAlarmService) {
                    Toast.makeText(
                        requireContext(),
                        "Another service is running",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    voicePasscodeInHomeBinding.txtActionStatus.text =
                        getString(R.string.tap_to_deactive)
                    voicePasscodeInHomeBinding.handIc.visibility = View.GONE
                    voicePasscodeInHomeBinding.round2.setImageResource(R.drawable.round2_active)
                    serviceSharedPreferences.edit()
                        .putBoolean(Constant.Service.VOICE_PASSCODE, true).apply()
                    val intent = Intent(requireContext(), MyService::class.java)
                    intent.putExtra(Constant.Service.RUNNING_SERVICE,Constant.Service.VOICE_PASSCODE_RUNNING)
                    requireContext().startService(intent)
                }

            } else {
                voicePasscodeInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                voicePasscodeInHomeBinding.handIc.visibility = View.VISIBLE
                voicePasscodeInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                serviceSharedPreferences.edit()
                    .putBoolean(Constant.Service.VOICE_PASSCODE, false).apply()
                val intent = Intent(requireContext(), MyService::class.java)
                requireContext().stopService(intent)
            }
        }
    }
}