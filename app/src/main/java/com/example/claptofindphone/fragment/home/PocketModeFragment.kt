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
import com.example.claptofindphone.activity.WaitActivity
import com.example.claptofindphone.databinding.DialogTouchPhoneBinding
import com.example.claptofindphone.databinding.FragmentPocketModeInHomeBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils

class PocketModeFragment : Fragment() {
private lateinit var pocketModeInHomeBinding: FragmentPocketModeInHomeBinding
    private lateinit var permissionController: PermissionController
    private var isOnWaitActivity: Boolean= false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionController=PermissionController()
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
            if (permissionController.isOverlayPermissionGranted(requireActivity())) {
                val runningService = SharePreferenceUtils.getRunningService()
                if (runningService == "") {
                    SharePreferenceUtils.setOpenHomeFragment(Constant.Service.POCKET_MODE)
                    onService(Constant.Service.POCKET_MODE_RUNNING)
                } else if (runningService != Constant.Service.POCKET_MODE_RUNNING) {
                    Toast.makeText(
                        requireContext(),
                        "Another service is running",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    pocketModeInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                    pocketModeInHomeBinding.handIc.visibility = View.VISIBLE
                    pocketModeInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                    SharePreferenceUtils.setRunningService("")
                    AnimationUtils.applyAnimations(pocketModeInHomeBinding.handIc)
                    AnimationUtils.stopAnimations(pocketModeInHomeBinding.round3)
                    SharePreferenceUtils.setIsWaited(false)
                    val intent = Intent(requireContext(), MyService::class.java)
                    requireContext().stopService(intent)
                }
            } else {
                permissionController.showInitialDialog(
                    requireActivity(), Constant.Permission.OVERLAY_PERMISSION
                )
            }

        }

    }

    override fun onResume() {
        super.onResume()
        val isOnPocketModeService =SharePreferenceUtils.getRunningService()
        if (isOnPocketModeService==Constant.Service.POCKET_MODE_RUNNING) {
            onService(Constant.Service.POCKET_MODE_RUNNING)
            AnimationUtils.stopAnimations(pocketModeInHomeBinding.handIc)
            AnimationUtils.applyWaveAnimation(pocketModeInHomeBinding.round3)

        } else {
            AnimationUtils.applyAnimations(pocketModeInHomeBinding.handIc)
            AnimationUtils.stopAnimations(pocketModeInHomeBinding.round3)
        }

        val isFirstTimeGetInPocketMode = SharePreferenceUtils.isShowPocketModeDialog()
        if (isFirstTimeGetInPocketMode) {
            val dialogBinding = DialogTouchPhoneBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this.requireActivity())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            // Show the dialog
            customDialog.show()
            SharePreferenceUtils.setIsShowPocketModeDialog(false)
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }
    }
    private fun onService(runningService: String) {
        AnimationUtils.stopAnimations(pocketModeInHomeBinding.handIc)
        AnimationUtils.applyWaveAnimation(pocketModeInHomeBinding.round3)
        pocketModeInHomeBinding.txtActionStatus.text =
            getString(R.string.tap_to_deactive)
        pocketModeInHomeBinding.handIc.visibility = View.GONE
        pocketModeInHomeBinding.round2.setImageResource(R.drawable.round2_active)
        SharePreferenceUtils.setRunningService(runningService)
        isOnWaitActivity = SharePreferenceUtils.isWaited()
        if (!isOnWaitActivity) {
            val intent = Intent(requireContext(), WaitActivity::class.java)
            intent.putExtra(Constant.Service.RUNNING_SERVICE, runningService)
            startActivity(intent)
            SharePreferenceUtils.setIsWaited(true)
        }

    }
}