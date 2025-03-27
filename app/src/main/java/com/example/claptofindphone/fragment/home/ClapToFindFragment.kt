package com.example.claptofindphone.fragment.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
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
import com.example.claptofindphone.databinding.DialogClapAndWhistleBinding
import com.example.claptofindphone.databinding.FragmentClapToFindInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils.getRunningService
import com.example.claptofindphone.utils.SharePreferenceUtils.isNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.isOnService
import com.example.claptofindphone.utils.SharePreferenceUtils.isShowClapAndWhistleDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsShowClapAndWhistleDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setOpenHomeFragment
import com.example.claptofindphone.utils.SharePreferenceUtils.setRunningService

class ClapToFindFragment : Fragment() {
    private var binding: FragmentClapToFindInHomeBinding? = null
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
        binding = FragmentClapToFindInHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnimation()
        binding!!.handClapButton.setOnClickListener { v -> handleClapButtonClick() }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            if (isNavigateFromSplash()) {
                setIsNavigateFromSplash(false)
                onService(Constant.Service.CLAP_AND_WHISTLE_RUNNING)
            } else {
                handleServiceState()
                showClapAndWhistleDialog()
            }
        } else {
            if (isOnService()) {
                stopService()
            } else {
                if (isNavigateFromSplash()) {
                    setIsNavigateFromSplash(false)
                    requestPermission()
                }
            }

        }
    }

    private fun handleClapButtonClick() {
        setOpenHomeFragment(Constant.Service.CLAP_TO_FIND_PHONE)
        val runningService = getRunningService()
        if (runningService == "") {
            if (checkPermission()) {
                onService(Constant.Service.CLAP_AND_WHISTLE_RUNNING)
            } else {
                requestPermission()
            }
        } else if (runningService != Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            Toast.makeText(requireContext(), R.string.other_service_running, Toast.LENGTH_LONG)
                .show()
        } else {
            if (isOnService()) {
                stopService()
            }
        }
    }

    private fun stopService() {
        setRunningService("")
        binding!!.txtActionStatus.setText(R.string.tap_to_active)
        binding!!.handIc.visibility = View.VISIBLE
        binding!!.round2.setImageResource(R.drawable.round2_passive)
        binding!!.handIc.startAnimation(anim)
        requireContext().stopService(Intent(requireContext(), MyService::class.java))
    }

    @SuppressLint("NewApi")
    private fun onService(runningService: String) {
        stopAnimation()
        binding!!.txtActionStatus.setText(R.string.tap_to_deactive)
        binding!!.handIc.visibility = View.GONE
        binding!!.round2.setImageResource(R.drawable.round2_active)
        if (!isOnService()) {
            setRunningService(runningService)
            val intent = Intent(requireContext(), MyService::class.java)
            intent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            requireContext().startService(intent)
        }

    }

    private fun setupAnimation() {
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
        anim!!.duration = 600
        anim!!.repeatCount = Animation.INFINITE
        anim!!.repeatMode = Animation.REVERSE
    }

    private fun stopAnimation() {
        if (anim != null) anim!!.cancel()
    }

    private fun handleServiceState() {
        val isOnClapService = getRunningService()
        if (isOnClapService == "") {
            binding!!.handIc.startAnimation(anim)
            setOpenHomeFragment(Constant.Service.CLAP_TO_FIND_PHONE)
        } else if (isOnClapService == Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            onService(Constant.Service.CLAP_AND_WHISTLE_RUNNING)
        } else {
            binding!!.handIc.startAnimation(anim)
        }
    }

    private fun showClapAndWhistleDialog() {
        if (isShowClapAndWhistleDialog()) {
            val dialogBinding = DialogClapAndWhistleBinding.inflate(
                layoutInflater
            )
            val customDialog = AlertDialog.Builder(requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()
            customDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            setIsShowClapAndWhistleDialog(false)
            dialogBinding.yesButton.setOnClickListener { v -> customDialog.dismiss() }
        }
    }

    private fun checkPermission(): Boolean {
        return permissionController!!.hasAudioPermission(requireActivity()) && permissionController!!.isOverlayPermissionGranted(
            requireActivity()
        )
    }

    private fun requestPermission() {
        permissionController!!.showInitialDialog(
            requireActivity(),
            Constant.Permission.BOTH_PERMISSION,
            Constant.Service.CLAP_TO_FIND_PHONE,
            Constant.Service.CLAP_AND_WHISTLE_RUNNING
        )
    }
}