package com.example.claptofindphone.fragment.home
import android.app.AlertDialog
import android.content.Intent
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
import com.example.claptofindphone.service.AnimationUtils
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils

class ClapToFindFragment : Fragment() {
    private lateinit var clapToFindInHomeBinding: FragmentClapToFindInHomeBinding
    private lateinit var permissionController: PermissionController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val runningService = SharePreferenceUtils.getRunningService()
                if (runningService=="") {
                    SharePreferenceUtils.setOpenHomeFragment(Constant.Service.CLAP_TO_FIND_PHONE)
                    onService(
                        Constant.Service.CLAP_AND_WHISTLE_RUNNING,
                    )
                }
                else if (runningService != Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
                    // check if other service running
                    Toast.makeText(
                        requireContext(),
                        R.string.other_service_running,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    SharePreferenceUtils.setRunningService("")
                    clapToFindInHomeBinding.txtActionStatus.text = getString(R.string.tap_to_active)
                    clapToFindInHomeBinding.handIc.visibility = View.VISIBLE
                    clapToFindInHomeBinding.round2.setImageResource(R.drawable.round2_passive)
                    SharePreferenceUtils.setRunningService("")
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
        val isOnClapService =SharePreferenceUtils.getRunningService()
        if (isOnClapService==Constant.Service.CLAP_AND_WHISTLE_RUNNING) {
            onService(Constant.Service.CLAP_AND_WHISTLE_RUNNING)
            AnimationUtils.stopAnimations(clapToFindInHomeBinding.handIc)
            AnimationUtils.applyWaveAnimation(clapToFindInHomeBinding.round3)
        } else {
            AnimationUtils.applyAnimations(clapToFindInHomeBinding.handIc)
            AnimationUtils.stopAnimations(clapToFindInHomeBinding.round3)
        }
        val isFirstTimeGetInClap =  SharePreferenceUtils.isShowClapAndWhistleDialog()
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
            SharePreferenceUtils.setIsShowClapAndWhistleDialog(false)
            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
        }

    }

    private fun onService(runningService: String) {
        AnimationUtils.stopAnimations(clapToFindInHomeBinding.handIc)
        AnimationUtils.applyWaveAnimation(clapToFindInHomeBinding.round3)
        clapToFindInHomeBinding.txtActionStatus.text =
            getString(R.string.tap_to_deactive)
        clapToFindInHomeBinding.handIc.visibility = View.GONE
        clapToFindInHomeBinding.round2.setImageResource(R.drawable.round2_active)
        SharePreferenceUtils.setRunningService(runningService)
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra(
            Constant.Service.RUNNING_SERVICE,
            runningService
        )
        requireContext().startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}