package com.example.claptofindphone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.databinding.CallThemeItemBinding
import com.example.claptofindphone.model.CallTheme

class CallThemeAdapter(val callThemeList: List<CallTheme>): RecyclerView.Adapter<CallThemeAdapter.CallThemeViewHolder>() {
    class CallThemeViewHolder(callThemeItemBinding: CallThemeItemBinding) :
        RecyclerView.ViewHolder(callThemeItemBinding.root) {
        val callThemeItemBinding: CallThemeItemBinding = callThemeItemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallThemeViewHolder {
        val callThemeItemBinding= CallThemeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CallThemeViewHolder(callThemeItemBinding)
    }

    override fun getItemCount(): Int {
      return callThemeList.size
    }

    override fun onBindViewHolder(holder: CallThemeViewHolder, position: Int) {
        val callThemeItem= callThemeList[position]
        holder.callThemeItemBinding.bgCallTheme.setBackgroundResource(callThemeItem.callThemeBg)
        holder.callThemeItemBinding.round2CallTheme.setImageResource(callThemeItem.callThemeRound2)
        holder.callThemeItemBinding.round1CallTheme.setImageResource(callThemeItem.callThemeRound1)
        holder.callThemeItemBinding.profileCallTheme.setImageResource(callThemeItem.callThemeProfile)
        holder.callThemeItemBinding.rejectCallThemeButton.setImageResource(callThemeItem.callThemeReject)
        holder.callThemeItemBinding.responseCallThemeButton.setImageResource(callThemeItem.callThemeResponse)
        holder.callThemeItemBinding.txtName.text=callThemeItem.callThemeName
        holder.callThemeItemBinding.txtPhone.text=callThemeItem.callThemePhone
        holder.callThemeItemBinding.premiumButton.setImageResource(callThemeItem.callThemePremium)
    }


}