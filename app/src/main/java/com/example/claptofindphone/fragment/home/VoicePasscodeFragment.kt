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
import com.example.claptofindphone.activity.VoicePasscodeActivity
import com.example.claptofindphone.activity.buildMinVersionO
import com.example.claptofindphone.databinding.DialogVoicePasscodeBinding
import com.example.claptofindphone.databinding.FragmentVoicePasscodeInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils
import com.example.claptofindphone.utils.SharePreferenceUtils.getRunningService
import com.example.claptofindphone.utils.SharePreferenceUtils.getVoicePasscode
import com.example.claptofindphone.utils.SharePreferenceUtils.isNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.isOnService
import com.example.claptofindphone.utils.SharePreferenceUtils.isShowVoicePasscodeDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsOnNotify
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsShowVoicePasscodeDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setOpenHomeFragment
import com.example.claptofindphone.utils.SharePreferenceUtils.setRunningService

class VoicePasscodeFragment : Fragment() {
    private var binding: FragmentVoicePasscodeInHomeBinding? = null
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
        binding = FragmentVoicePasscodeInHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnimation()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        handleNavigationFromSplash()
        handleVoicePasscodeService()
        showFirstTimeDialog()
    }

    private fun setupClickListeners() {
        binding!!.voicePasscodeButton.setOnClickListener { v ->
            val runningService = getRunningService()
            if (runningService.isEmpty()) {
                setOpenHomeFragment(Constant.Service.VOICE_PASSCODE)
                checkPermissionToRun()
            } else if (runningService != Constant.Service.VOICE_PASSCODE_RUNNING) {
                Toast.makeText(requireContext(), R.string.other_service_running, Toast.LENGTH_LONG)
                    .show()
            } else {
                Log.d(TAG, "setupClickListeners: stop service in voice")
                stopVoicePasscodeService()
            }
        }
    }

    private fun stopVoicePasscodeService() {
        setRunningService("")
        binding!!.txtActionStatus.setText(R.string.tap_to_active)
        binding!!.handIc.visibility = View.VISIBLE
        binding!!.round2.setImageResource(R.drawable.round2_passive)
        binding!!.handIc.startAnimation(anim)
        requireContext().stopService(Intent(requireContext(), MyService::class.java))
    }

    private fun handleNavigationFromSplash() {
        if (isNavigateFromSplash()) {
            setIsNavigateFromSplash(false)
            checkPermissionToRun()
        }
    }

    private fun handleVoicePasscodeService() {
        val isOnVoicePasscodeService = getRunningService()
        if (isOnVoicePasscodeService=="") {
            binding!!.handIc.startAnimation(anim)
            setOpenHomeFragment(Constant.Service.VOICE_PASSCODE)
        } else if (isOnVoicePasscodeService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            if (permissionController!!.isInternetAvailable(requireActivity())) {
                if (getVoicePasscode() != Constant.DEFAULT_PASSCODE) {
                    startVoicePasscodeService(Constant.Service.VOICE_PASSCODE_RUNNING)
                } else {
                    navigateToVoicePasscodeActivity()
                }
            } else {
                showInternetRequiredToast()
            }
        }
    }

    private fun showFirstTimeDialog() {
        if (isShowVoicePasscodeDialog()) {
            val dialogBinding = DialogVoicePasscodeBinding.inflate(
                layoutInflater
            )
            val customDialog = AlertDialog.Builder(requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()
            customDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            setIsShowVoicePasscodeDialog(false)
            dialogBinding.yesButton.setOnClickListener { v -> customDialog.dismiss() }
        }
    }

    private fun startVoicePasscodeService(runningService: String) {
        stopAnimation()
        binding!!.txtActionStatus.setText(R.string.tap_to_deactive)
        binding!!.handIc.visibility = View.GONE
        binding!!.round2.setImageResource(R.drawable.round2_active)
        Log.d(TAG, "startVoicePasscodeService:${getRunningService()} ")
        if (!isOnService()){
            Log.d(TAG, "startVoicePasscodeService1:${getRunningService()} ")
            setRunningService(Constant.Service.VOICE_PASSCODE_RUNNING)
            val intent = Intent(requireContext(), MyService::class.java)
            intent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            requireContext().startService(intent)
        }

    }

    private fun setupAnimation() {
        anim = ScaleAnimation(
            1.0f, 1.3f, 1.0f, 1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim!!.duration = 600
        anim!!.repeatCount = Animation.INFINITE
        anim!!.repeatMode = Animation.REVERSE
    }

    private fun stopAnimation() {
        if (anim != null) {
            anim!!.cancel()
        }
    }

    private fun checkPermissionToRun() {
        if (!permissionController!!.isInternetAvailable(requireActivity())) {
            showInternetRequiredToast()
            return
        }

        if (permissionController!!.hasAudioPermission(requireActivity()) &&
            permissionController!!.isOverlayPermissionGranted(requireActivity())
        ) {
            if (getVoicePasscode() == Constant.DEFAULT_PASSCODE) {
                navigateToVoicePasscodeActivity()
            } else {
                setIsOnNotify(true)
                startVoicePasscodeService(Constant.Service.VOICE_PASSCODE_RUNNING)
            }
        } else {
            permissionController!!.showInitialDialog(
                requireActivity(),
                Constant.Permission.BOTH_PERMISSION,
                Constant.Service.VOICE_PASSCODE,
                Constant.Service.VOICE_PASSCODE_RUNNING
            )
        }
    }

    private fun navigateToVoicePasscodeActivity() {
        startActivity(Intent(requireContext(), VoicePasscodeActivity::class.java))
    }

    private fun showInternetRequiredToast() {
        Toast.makeText(
            requireContext(),
            R.string.connect_internet_to_use_this_feature,
            Toast.LENGTH_SHORT
        ).show()
    }
}