package com.example.claptofindphone.fragment.home

import android.app.AlertDialog
import android.content.Intent
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
import com.example.claptofindphone.activity.buildMinVersionO
import com.example.claptofindphone.databinding.DialogPocketModeBinding
import com.example.claptofindphone.databinding.FragmentPocketModeInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils.getRunningService
import com.example.claptofindphone.utils.SharePreferenceUtils.isNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.isShowPocketModeDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.isWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsNavigateFromSplash
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsOnNotify
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsShowPocketModeDialog
import com.example.claptofindphone.utils.SharePreferenceUtils.setIsWaited
import com.example.claptofindphone.utils.SharePreferenceUtils.setOpenHomeFragment
import com.example.claptofindphone.utils.SharePreferenceUtils.setRunningService

class PocketModeFragment : Fragment() {
    private var binding: FragmentPocketModeInHomeBinding? = null
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
        binding = FragmentPocketModeInHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAnimation()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        handleNavigationFromSplash()
        handlePocketModeService()
        showFirstTimeDialog()
    }

    private fun setupClickListeners() {
        binding!!.pocketModeButton.setOnClickListener { v ->
            setOpenHomeFragment(Constant.Service.POCKET_MODE)
            val runningService = getRunningService()
            if (runningService.isEmpty()) {
                checkPermissionToRun()
            } else if (runningService != Constant.Service.POCKET_MODE_RUNNING) {
                Toast.makeText(requireContext(), R.string.other_service_running, Toast.LENGTH_LONG)
                    .show()
            } else {
                stopPocketModeService()
            }
        }
    }

    private fun stopPocketModeService() {
        binding!!.txtActionStatus.setText(R.string.tap_to_active)
        binding!!.handIc.visibility = View.VISIBLE
        binding!!.round2.setImageResource(R.drawable.round2_passive)
        setRunningService("")
        binding!!.handIc.startAnimation(anim)
        setIsWaited(false)
        requireContext().stopService(Intent(requireContext(), MyService::class.java))
    }

    private fun handleNavigationFromSplash() {
        if (isNavigateFromSplash()) {
            setIsNavigateFromSplash(false)
            checkPermissionToRun()
        }
    }

    private fun handlePocketModeService() {
        val isOnPocketModeService = getRunningService()
        if (isOnPocketModeService.isEmpty()) {
            binding!!.handIc.startAnimation(anim)
            setOpenHomeFragment(Constant.Service.POCKET_MODE)
        } else if (isOnPocketModeService == Constant.Service.POCKET_MODE_RUNNING) {
            startPocketModeService(Constant.Service.POCKET_MODE_RUNNING)
        } else {
            binding!!.handIc.startAnimation(anim)
        }
    }

    private fun showFirstTimeDialog() {
        if (isShowPocketModeDialog()) {
            val dialogBinding = DialogPocketModeBinding.inflate(
                layoutInflater
            )
            val customDialog = AlertDialog.Builder(requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()
            customDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            setIsShowPocketModeDialog(false)
            dialogBinding.yesButton.setOnClickListener { v -> customDialog.dismiss() }
        }
    }

    private fun startPocketModeService(runningService: String) {
        stopAnimation()
        binding!!.txtActionStatus.setText(R.string.tap_to_deactive)
        binding!!.handIc.visibility = View.GONE
        binding!!.round2.setImageResource(R.drawable.round2_active)




        if (isWaited()) {
            setIsWaited(false)
            val intent = Intent(requireContext(), MyService::class.java)
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
        if (permissionController!!.isOverlayPermissionGranted(requireActivity())) {
            if (!isWaited()) {
                setIsWaited(true)
            }
            setRunningService(Constant.Service.POCKET_MODE_RUNNING)
            setIsOnNotify(true)
            startPocketModeService(Constant.Service.POCKET_MODE_RUNNING)
        } else {
            permissionController!!.showInitialDialog(
                requireActivity(),
                Constant.Permission.OVERLAY_PERMISSION,
                Constant.Service.POCKET_MODE,
                Constant.Service.POCKET_MODE_RUNNING
            )
        }
    }
}