package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.WaitActivity
import com.example.claptofindphone.databinding.DialogTouchPhoneBinding
import com.example.claptofindphone.databinding.FragmentDontTouchMyPhoneInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyServiceNoMicro
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils
import com.example.claptofindphone.utils.SharePreferenceUtils.getRunningService
import com.example.claptofindphone.utils.SharePreferenceUtils.isNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.isOnService
import com.example.claptofindphone.utils.SharePreferenceUtils.isShowTouchPhoneDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.isWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsOnNotify
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsShowTouchPhoneDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setOpenHomeFragment
import com.example.claptofindphone.utils.SharePreferenceUtils.setRunningService

class DontTouchMyPhoneFragment : Fragment() {
    private var binding: FragmentDontTouchMyPhoneInHomeBinding? = null
    private var anim: ScaleAnimation? = null
    private var permissionController: PermissionController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionController = PermissionController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDontTouchMyPhoneInHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnimation()
        binding!!.touchPhoneButton.setOnClickListener { v -> handleTouchPhoneButtonClick() }
    }

    private fun handleTouchPhoneButtonClick() {
        setOpenHomeFragment(Constant.Service.DONT_TOUCH_MY_PHONE)
        val runningService = getRunningService()
        if (runningService == "") {
            if (permissionController!!.isOverlayPermissionGranted(requireActivity())) {
                onService(Constant.Service.TOUCH_PHONE_RUNNING)
            } else {
                requestPermission()
            }
        } else if (runningService != Constant.Service.TOUCH_PHONE_RUNNING) {
            Toast.makeText(requireContext(), R.string.other_service_running, Toast.LENGTH_LONG)
                .show()
        } else {
            stopService()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNavigateFromSplash()) {
            setIsNavigateFromSplash(false)
            if (permissionController!!.isOverlayPermissionGranted(requireActivity())) {
                onService(Constant.Service.TOUCH_PHONE_RUNNING)
            } else {
                requestPermission()
            }
        } else {
            handleServiceState()
            showFirstTimeDialog()
        }
    }

    private fun handleServiceState() {
        val runningService = getRunningService()
        if (runningService == "") {
            binding!!.handIc.startAnimation(anim)
            setOpenHomeFragment(Constant.Service.DONT_TOUCH_MY_PHONE)
        } else if (runningService == Constant.Service.TOUCH_PHONE_RUNNING) {
            if (permissionController!!.isOverlayPermissionGranted(requireActivity())) {
                onService(Constant.Service.TOUCH_PHONE_RUNNING)
            } else {
                if (isOnService()) {
                    stopService()
                }
            }
        } else {
            binding!!.handIc.startAnimation(anim)
        }
    }


    private fun onService(runningService: String) {
        stopAnimation()
        binding!!.txtActionStatus.setText(R.string.tap_to_deactive)
        binding!!.handIc.visibility = View.GONE
        binding!!.round2.setImageResource(R.drawable.round2_active)
        if (!isWaited()) {
           setRunningService(runningService)
            setIsWaited(true)
            val intentService = Intent(requireContext(), MyServiceNoMicro::class.java)
            intentService.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            requireContext().startService(intentService)
            val intent = Intent(
                requireContext(),
                WaitActivity::class.java
            )
            intent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            startActivity(intent)
        }
    }

    private fun stopService() {
        binding!!.txtActionStatus.setText(R.string.tap_to_active)
        binding!!.handIc.visibility = View.VISIBLE
        binding!!.round2.setImageResource(R.drawable.round2_passive)
        binding!!.handIc.startAnimation(anim)
        setIsWaited(false)
        val intent = Intent(requireContext(), MyServiceNoMicro::class.java)
        requireContext().stopService(intent)
    }

    private fun requestPermission() {
        permissionController!!.showInitialDialog(
            requireActivity(),
            Constant.Permission.OVERLAY_PERMISSION,
            Constant.Service.DONT_TOUCH_MY_PHONE,
            Constant.Service.TOUCH_PHONE_RUNNING
        )
    }

    private fun showFirstTimeDialog() {
        if (isShowTouchPhoneDialog()) {
            val dialogBinding = DialogTouchPhoneBinding.inflate(
                layoutInflater
            )
            val customDialog = AlertDialog.Builder(requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()
            customDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            setIsShowTouchPhoneDialog(false)
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
        if (anim != null) anim!!.cancel()
    }
}