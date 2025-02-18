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
import com.example.claptofindphone.databinding.DialogTouchPhoneBinding
import com.example.claptofindphone.databinding.FragmentDontTouchMyPhoneInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService


class DontTouchMyPhoneFragment : Fragment() {
    private lateinit var serviceSharedPreferences: SharedPreferences
    private lateinit var touchPhoneInHomeBinding: FragmentDontTouchMyPhoneInHomeBinding

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
        touchPhoneInHomeBinding =
            FragmentDontTouchMyPhoneInHomeBinding.inflate(inflater, container, false)
        return touchPhoneInHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        touchPhoneInHomeBinding.touchPhoneButton.setOnClickListener {
            serviceSharedPreferences.edit().putString(Constant.Service.RUNNING_SERVICE,Constant.Service.TOUCH_PHONE_RUNNING).apply()
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
            if (!isOnDontTouchMyPhoneService) {
                // check if other service running
                if (isOnVoicePasscodeService || isOnPocketModeService || isOnClapService || isOnChargerAlarmService) {
                    Toast.makeText(
                        requireContext(),
                        "Another service is running",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onService(
                        Constant.Service.DONT_TOUCH_MY_PHONE,
                        Constant.Service.TOUCH_PHONE_RUNNING)
                }

            } else {
                touchPhoneInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                touchPhoneInHomeBinding.handIc.visibility = View.VISIBLE
                touchPhoneInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                serviceSharedPreferences.edit()
                    .putBoolean(Constant.Service.DONT_TOUCH_MY_PHONE, false).apply()
                AnimationUtils.applyAnimations(touchPhoneInHomeBinding.handIc)
                AnimationUtils.stopAnimations(touchPhoneInHomeBinding.round3)
                val intent = Intent(requireContext(), MyService::class.java)
                requireContext().stopService(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isOnTouchPhoneService =
            serviceSharedPreferences.getBoolean(Constant.Service.DONT_TOUCH_MY_PHONE, false)
        if (isOnTouchPhoneService) {
            onService(
                Constant.Service.DONT_TOUCH_MY_PHONE,
                Constant.Service.TOUCH_PHONE_RUNNING
            )
            AnimationUtils.stopAnimations(touchPhoneInHomeBinding.handIc)
            AnimationUtils.applyWaveAnimation(touchPhoneInHomeBinding.round3)

        } else {
            AnimationUtils.applyAnimations(touchPhoneInHomeBinding.handIc)
            AnimationUtils.stopAnimations(touchPhoneInHomeBinding.round3)
        }

        val firstTimeSharedPreferences= this.requireActivity().getSharedPreferences(
            Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES,
            MODE_PRIVATE
        )
        val isFirstTimeGetInVoicePasscode=firstTimeSharedPreferences.getBoolean(Constant.SharePres.FIRST_TIME_GET_IN_TOUCH_PHONE,true)
        if (isFirstTimeGetInVoicePasscode){
            val dialogBinding = DialogTouchPhoneBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            firstTimeSharedPreferences.edit().putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_TOUCH_PHONE,false).apply()
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }
    private fun onService(typeOfService: String, typeOfServiceIntent: String) {
        AnimationUtils.stopAnimations(touchPhoneInHomeBinding.handIc)
        AnimationUtils.applyWaveAnimation(touchPhoneInHomeBinding.round3)
        touchPhoneInHomeBinding.txtActionStatus.text =
            getString(R.string.tap_to_deactive)
        touchPhoneInHomeBinding.handIc.visibility = View.GONE
        touchPhoneInHomeBinding.round2.setImageResource(R.drawable.round2_active)
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