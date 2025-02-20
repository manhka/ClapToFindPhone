package com.example.claptofindphone.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.DialogWatchAdBinding
import com.example.claptofindphone.databinding.FlashlightItemBinding
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.utils.SharePreferenceUtils

class FlashlightAdapter(
    val context: Context,
    val flashlightList: List<Flashlight>,
    val onItemSelected: (Flashlight) -> Unit
) : RecyclerView.Adapter<FlashlightAdapter.FlashlightViewHolder>() {


    val selectedFlashlight = SharePreferenceUtils.getFlashName()
    var selectedPosition = flashlightList.indexOfFirst { it.flashlightName == selectedFlashlight }

    class FlashlightViewHolder(itemFlashlightItemBinding: FlashlightItemBinding) :
        RecyclerView.ViewHolder(itemFlashlightItemBinding.root) {
        val itemFlashlightItemBinding: FlashlightItemBinding = itemFlashlightItemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashlightViewHolder {
        val flashlightItemBinding =
            FlashlightItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlashlightViewHolder(flashlightItemBinding)
    }

    override fun getItemCount(): Int {
        return flashlightList.size
    }

    override fun onBindViewHolder(holder: FlashlightViewHolder, position: Int) {
        val flashlightItem = flashlightList[position]
        val isPremiumVisible = SharePreferenceUtils.isPremiumVisible(position)

        holder.itemFlashlightItemBinding.flashlightBg.setBackgroundResource(flashlightItem.flashlightBg)
        holder.itemFlashlightItemBinding.txtFlashlightName.text = flashlightItem.flashlightName
        holder.itemFlashlightItemBinding.selectedIc.setImageResource(flashlightItem.flashlightSelected)

        if (isPremiumVisible) {
            holder.itemFlashlightItemBinding.premiumButton.setImageResource(flashlightItem.flashlightPremium)
        } else {
            flashlightItem.flashlightPremium=0
            holder.itemFlashlightItemBinding.premiumButton.setImageResource(0)
        }

        if (position == selectedPosition) {
            holder.itemFlashlightItemBinding.flashlightBg.setBackgroundResource(R.drawable.bg_active_item)
            holder.itemFlashlightItemBinding.selectedIc.visibility = View.VISIBLE
            holder.itemFlashlightItemBinding.premiumButton.visibility = View.GONE
        } else {
            holder.itemFlashlightItemBinding.flashlightBg.setBackgroundResource(flashlightItem.flashlightBg)
            holder.itemFlashlightItemBinding.selectedIc.visibility = View.GONE
            holder.itemFlashlightItemBinding.premiumButton.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            if (flashlightItem.flashlightPremium!=0){
                val dialogBinding = DialogWatchAdBinding.inflate(LayoutInflater.from(context))
                // Create an AlertDialog with the inflated ViewBinding root
                val customDialog = AlertDialog.Builder(context)
                    .setView(dialogBinding.root)
                    .setCancelable(false)
                    .create()
                customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                // Show the dialog
                customDialog.show()
                dialogBinding.watchAdTitle.text=context.getString(R.string.dialog_flashlight_title)
                dialogBinding.watchAdsContent.text=context.getString(R.string.dialog_flashlight_content)
                dialogBinding.watchAdsButton.setOnClickListener {
                    SharePreferenceUtils.setIsPremiumVisible(position,false)
                    selectedPosition = position
                    notifyDataSetChanged()
                    customDialog.dismiss()
                }
                dialogBinding.exitButton.setOnClickListener {
                    customDialog.dismiss()
                }
            }else{
                selectedPosition = position
                notifyDataSetChanged()
            }
            onItemSelected(flashlightItem)
        }
        }

}