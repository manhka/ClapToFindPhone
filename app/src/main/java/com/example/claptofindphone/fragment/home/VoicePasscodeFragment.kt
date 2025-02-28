package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.VoicePasscodeActivity
import com.example.claptofindphone.databinding.DialogVoicePasscodeBinding
import com.example.claptofindphone.databinding.FragmentVoicePasscodeInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils

class VoicePasscodeFragment : Fragment() {
    private lateinit var voicePasscodeInHomeBinding: FragmentVoicePasscodeInHomeBinding
    private lateinit var permissionController: PermissionController
    private var anim: ScaleAnimation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionController = PermissionController()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnim()
        voicePasscodeInHomeBinding.voicePasscodeButton.setOnClickListener {
            if (permissionController.hasAudioPermission(requireActivity()) && permissionController.isOverlayPermissionGranted(
                    requireActivity()
                )
            ) {
                if (permissionController.isInternetAvailable(requireActivity())) {
                    if (SharePreferenceUtils.getVoicePasscode() == "") {
                        val intent = Intent(requireContext(), VoicePasscodeActivity::class.java)
                        startActivity(intent)
                    } else {
                        val runningService = SharePreferenceUtils.getRunningService()
                        if (runningService == "") {
                            SharePreferenceUtils.setOpenHomeFragment(Constant.Service.VOICE_PASSCODE)
                            onService(
                                Constant.Service.VOICE_PASSCODE_RUNNING,
                            )
                        } else if (runningService != Constant.Service.VOICE_PASSCODE_RUNNING) {
                            Toast.makeText(
                                requireContext(),
                                R.string.other_service_running,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            SharePreferenceUtils.setRunningService("")
                            voicePasscodeInHomeBinding.txtActionStatus.text =
                                getString(R.string.tap_to_active)
                            voicePasscodeInHomeBinding.handIc.visibility = View.VISIBLE
                            voicePasscodeInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                            voicePasscodeInHomeBinding.handIc.startAnimation(anim)
                            val intent = Intent(requireContext(), MyService::class.java)
                            requireContext().stopService(intent)
                        }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.connect_internet_to_use_this_feature,
                        Toast.LENGTH_SHORT
                    ).show()
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
        val isOnVoicePasscodeService = SharePreferenceUtils.getRunningService()
        if (isOnVoicePasscodeService == Constant.Service.VOICE_PASSCODE_RUNNING) {
            if (permissionController.isInternetAvailable(requireActivity())){
                onService(Constant.Service.VOICE_PASSCODE_RUNNING)

            }else{
                SharePreferenceUtils.setRunningService("")
                Toast.makeText(
                    requireContext(),
                    R.string.connect_internet_to_use_this_feature,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            voicePasscodeInHomeBinding.handIc.startAnimation(anim)
        }

        val isFirstTimeGetInVoicePasscode = SharePreferenceUtils.isShowVoicePasscodeDialog()
        if (isFirstTimeGetInVoicePasscode) {
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
            SharePreferenceUtils.setIsShowVoicePasscodeDialog(false)
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }

    private fun onService(runningService: String) {
       stopAnim()
        voicePasscodeInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_deactive)
        voicePasscodeInHomeBinding.handIc.visibility = View.GONE
        voicePasscodeInHomeBinding.round2.setImageResource(R.drawable.round2_active)
        SharePreferenceUtils.setRunningService(runningService)
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra(
            Constant.Service.RUNNING_SERVICE,
            runningService
        )
        requireContext().startService(intent)
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