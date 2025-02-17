package com.example.claptofindphone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.DialogWatchAdBinding
import com.example.claptofindphone.databinding.VibrateItemBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Vibrate

class VibrateAdapter(
    val context: Context,
    val vibrateList: List<Vibrate>
,val onItemSelected: (Vibrate) -> Unit):RecyclerView.Adapter<VibrateAdapter.VibrateViewHolder>() {
    val vibrateSharedPreferences =
        context.getSharedPreferences(Constant.SharePres.VIBRATE_SHARE_PRES, MODE_PRIVATE)
    val selectedVibrate = vibrateSharedPreferences.getString(
        Constant.SharePres.ACTIVE_VIBRATE_NAME,
        Constant.Vibrate.default
    )
    var selectedPosition = vibrateList.indexOfFirst { it.vibrateName == selectedVibrate }
    class VibrateViewHolder(vibrateItemBinding: VibrateItemBinding):RecyclerView.ViewHolder(vibrateItemBinding.root){
        val vibrateItemBinding:VibrateItemBinding=vibrateItemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VibrateViewHolder {
        val vibrateItemBinding=VibrateItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VibrateViewHolder(vibrateItemBinding)
    }

    override fun getItemCount(): Int {
       return vibrateList.size
    }

    override fun onBindViewHolder(holder: VibrateViewHolder, position: Int) {
       val vibrate=vibrateList[position]
        val isPremiumVisible = vibrateSharedPreferences.getBoolean("isPremiumVisible_$position", true)
        holder.vibrateItemBinding.vibrateBg.setBackgroundResource(vibrate.vibrateBg)
        holder.vibrateItemBinding.txtVibrateName.text=vibrate.vibrateName
        holder.vibrateItemBinding.selectedIc.setImageResource(vibrate.vibrateSelected)
        if (isPremiumVisible) {
            holder.vibrateItemBinding.premiumButton.setImageResource(vibrate.vibratePremium)
        } else {
            vibrate.vibratePremium=0
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
            if (vibrate.vibratePremium!=0){
                val dialogBinding = DialogWatchAdBinding.inflate(LayoutInflater.from(context))
                // Create an AlertDialog with the inflated ViewBinding root
                val customDialog = AlertDialog.Builder(context)
                    .setView(dialogBinding.root)
                    .setCancelable(false)
                    .create()
                customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                // Show the dialog
                customDialog.show()
                dialogBinding.watchAdTitle.text=context.getString(R.string.dialog_vibrate_title)
                dialogBinding.watchAdsContent.text=context.getString(R.string.dialog_vibrate_content)
                dialogBinding.watchAdsButton.setOnClickListener {
                    vibrateSharedPreferences.edit().putBoolean("isPremiumVisible_$position", false).apply()
//                    holder.vibrateItemBinding.premiumButton.setImageResource(0)
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
            onItemSelected(vibrate)
        }
    }

}