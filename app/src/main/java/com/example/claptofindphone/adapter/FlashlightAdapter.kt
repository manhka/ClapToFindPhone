package com.example.claptofindphone.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.FlashlightItemBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Flashlight

class FlashlightAdapter(
    val context: Context,
    val flashlightList: List<Flashlight>,
    val onItemSelected: (Flashlight) -> Unit
) : RecyclerView.Adapter<FlashlightAdapter.FlashlightViewHolder>() {

    val flashlightSharedPreferences =
        context.getSharedPreferences(Constant.SharePres.FLASHLIGHT_SHARE_PRES, MODE_PRIVATE)
    val selectedFlashlight = flashlightSharedPreferences.getString(
        Constant.SharePres.ACTIVE_FLASHLIGHT_NAME,
        Constant.Flashlight.default
    )
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

        holder.itemFlashlightItemBinding.flashlightBg.setBackgroundResource(flashlightItem.flashlightBg)
        holder.itemFlashlightItemBinding.txtFlashlightName.text = flashlightItem.flashlightName
        holder.itemFlashlightItemBinding.selectedIc.setImageResource(flashlightItem.flashlightSelected)
        holder.itemFlashlightItemBinding.premiumButton.setImageResource(flashlightItem.flashlightPremium)

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
            selectedPosition = position
            notifyDataSetChanged()
            onItemSelected(flashlightItem)
        }
    }
}