package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.Context
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
import com.example.claptofindphone.databinding.DialogClapAndWhistleBinding
import com.example.claptofindphone.databinding.FragmentClapToFindInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService

class ClapToFindFragment : Fragment() {
    private lateinit var serviceSharedPreferences: SharedPreferences
    private lateinit var clapToFindInHomeBinding: FragmentClapToFindInHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceSharedPreferences = requireActivity().getSharedPreferences(
            Constant.SharePres.SERVICE_SHARE_PRES,
            Context.MODE_PRIVATE
        )
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
        val isOnVoicePasscodeService =
            serviceSharedPreferences.getBoolean(Constant.Service.VOICE_PASSCODE, false)
        val isOnDontTouchMyPhoneService =
            serviceSharedPreferences.getBoolean(Constant.Service.DONT_TOUCH_MY_PHONE, false)
        val isOnPocketModeService =
            serviceSharedPreferences.getBoolean(Constant.Service.POCKET_MODE, false)
        val isOnChargerAlarmService =
            serviceSharedPreferences.getBoolean(Constant.Service.CHARGER_PHONE, false)
        clapToFindInHomeBinding.handClapButton.setOnClickListener {
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

                    clapToFindInHomeBinding.txtActionStatus.text =
                        getString(R.string.tap_to_deactive)
                    clapToFindInHomeBinding.handIc.visibility = View.GONE
                    clapToFindInHomeBinding.round2.setImageResource(R.drawable.round2_active)
                    serviceSharedPreferences.edit()
                        .putBoolean(Constant.Service.CLAP_TO_FIND_PHONE, true).apply()
                    val intent = Intent(requireContext(), MyService::class.java)
                    requireContext().startService(intent)
                }

            } else {
                clapToFindInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                clapToFindInHomeBinding.handIc.visibility = View.VISIBLE
                clapToFindInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                serviceSharedPreferences.edit()
                    .putBoolean(Constant.Service.CLAP_TO_FIND_PHONE, false).apply()
                val intent = Intent(requireContext(), MyService::class.java)
                requireContext().stopService(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val firstTimeSharedPreferences= this.requireActivity().getSharedPreferences(Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES,MODE_PRIVATE)
        val isFirstTimeGetInClap=firstTimeSharedPreferences.getBoolean(Constant.SharePres.FIRST_TIME_GET_IN_CLAP_AND_WHISTLE,true)
        if (isFirstTimeGetInClap){
            val dialogBinding = DialogClapAndWhistleBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            firstTimeSharedPreferences.edit().putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_CLAP_AND_WHISTLE,false).apply()
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }

    }

}