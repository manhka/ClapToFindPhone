package com.example.claptofindphone.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.DialogWatchAdBinding
import com.example.claptofindphone.databinding.VibrateItemBinding
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.service.VibrateController
import com.example.claptofindphone.utils.SharePreferenceUtils

class VibrateAdapter(
    val context: Context,
    val vibrateList: List<Vibrate>, val onItemSelected: (Vibrate) -> Unit
) : RecyclerView.Adapter<VibrateAdapter.VibrateViewHolder>() {
    val selectedVibrate = SharePreferenceUtils.getVibrateId()
    var selectedPosition = vibrateList.indexOfFirst { it.vibrateId == selectedVibrate }
val permissionController = PermissionController()
    class VibrateViewHolder(vibrateItemBinding: VibrateItemBinding) :
        RecyclerView.ViewHolder(vibrateItemBinding.root) {
        val vibrateItemBinding: VibrateItemBinding = vibrateItemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VibrateViewHolder {
        val vibrateItemBinding =
            VibrateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VibrateViewHolder(vibrateItemBinding)
    }

    override fun getItemCount(): Int {
        return vibrateList.size
    }

    override fun onBindViewHolder(holder: VibrateViewHolder, position: Int) {
        val vibrate = vibrateList[position]
        val isPremiumVisible = SharePreferenceUtils.isVibratePremiumVisible(position)
        holder.vibrateItemBinding.vibrateBg.setBackgroundResource(vibrate.vibrateBg)
        holder.vibrateItemBinding.txtVibrateName.text = vibrate.vibrateName
        holder.vibrateItemBinding.selectedIc.setImageResource(vibrate.vibrateSelected)
        if (isPremiumVisible) {
            holder.vibrateItemBinding.premiumButton.setImageResource(vibrate.vibratePremium)
        } else {
            vibrate.vibratePremium = 0
            holder.vibrateItemBinding.premiumButton.setImageResource(0)
        }

        if (position == selectedPosition) {
            holder.vibrateItemBinding.vibrateBg.setBackgroundResource(R.drawable.bg_active_item)
            holder.vibrateItemBinding.selectedIc.visibility = View.VISIBLE
        } else {
            holder.vibrateItemBinding.vibrateBg.setBackgroundResource(vibrate.vibrateBg)
            holder.vibrateItemBinding.selectedIc.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            VibrateController.stopVibrating()
            if (vibrate.vibratePremium != 0) {
                if (permissionController.isInternetAvailable(context)){
                    val dialogBinding = DialogWatchAdBinding.inflate(LayoutInflater.from(context))
                    // Create an AlertDialog with the inflated ViewBinding root
                    val customDialog = AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setCancelable(false)
                        .create()
                    customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    // Show the dialog
                    customDialog.show()
                    dialogBinding.watchAdTitle.text = context.getString(R.string.dialog_vibrate_title)
                    dialogBinding.watchAdsContent.text =
                        context.getString(R.string.dialog_vibrate_content)
                    dialogBinding.watchAdsButton.setOnClickListener {
                        SharePreferenceUtils.setIsVibratePremiumVisible(position, false)
//                    holder.vibrateItemBinding.premiumButton.setImageResource(0)
                        selectedPosition = position
                        notifyDataSetChanged()
                        customDialog.dismiss()
                        onItemSelected(vibrate)
                    }
                    dialogBinding.exitButton.setOnClickListener {
                        customDialog.dismiss()
                    }
                }else{
                  showCustomToast()
                }

            } else {
                onItemSelected(vibrate)
                selectedPosition = position
                notifyDataSetChanged()

            }

        }
    }
    private fun showCustomToast() {
        // Inflate layout custom_toast.xml
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.custom_toast, null)
        // Tạo và hiển thị Toast
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

}