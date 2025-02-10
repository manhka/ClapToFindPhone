package com.example.claptofindphone.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
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
        holder.vibrateItemBinding.vibrateBg.setBackgroundResource(vibrate.vibrateBg)
        holder.vibrateItemBinding.txtVibrateName.text=vibrate.vibrateName
        holder.vibrateItemBinding.selectedIc.setImageResource(vibrate.vibrateSelected)
        holder.vibrateItemBinding.premiumButton.setImageResource(vibrate.vibratePremium)
        if (position == selectedPosition) {
            holder.vibrateItemBinding.vibrateBg.setBackgroundResource(R.drawable.bg_active_item)
            holder.vibrateItemBinding.selectedIc.visibility = View.VISIBLE
            holder.vibrateItemBinding.premiumButton.visibility = View.GONE
        } else {
            holder.vibrateItemBinding.vibrateBg.setBackgroundResource(vibrate.vibrateBg)
            holder.vibrateItemBinding.selectedIc.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onItemSelected(vibrate)
        }
    }

}