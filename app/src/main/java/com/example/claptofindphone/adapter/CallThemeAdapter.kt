package com.example.claptofindphone.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.EditThemeActivity
import com.example.claptofindphone.databinding.CallThemeItemBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.utils.SharePreferenceUtils

class CallThemeAdapter(val context: Context,
    val callThemeList: List<CallTheme>): RecyclerView.Adapter<CallThemeAdapter.CallThemeViewHolder>() {
    class CallThemeViewHolder(callThemeItemBinding: CallThemeItemBinding) :
        RecyclerView.ViewHolder(callThemeItemBinding.root) {
        val callThemeItemBinding: CallThemeItemBinding = callThemeItemBinding
    }
    val callThemeName = SharePreferenceUtils.getThemeName()
    val name=SharePreferenceUtils.getName()
    val phone=SharePreferenceUtils.getPhone()
    val selectedPosition = callThemeList.indexOfFirst { it.themeName == callThemeName }
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
        holder.callThemeItemBinding.txtName.text=name
        holder.callThemeItemBinding.txtPhone.text=phone
        holder.callThemeItemBinding.premiumButton.setImageResource(callThemeItem.callThemePremium)
        if (position == selectedPosition) {
            holder.callThemeItemBinding.premiumButton.setImageResource(R.drawable.active_theme_ic)
        } else {
            holder.callThemeItemBinding.premiumButton.setImageResource(0)
        }
        holder.itemView.setOnClickListener {
            val intent= Intent(context,EditThemeActivity::class.java)
            intent.putExtra("call_theme",callThemeItem)
            context.startActivity(intent)
        }
    }
}