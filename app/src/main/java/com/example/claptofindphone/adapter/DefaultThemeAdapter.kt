package com.example.claptofindphone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.databinding.DefaultThemeItemBinding
import com.example.claptofindphone.model.DefaultTheme

class DefaultThemeAdapter(val defaultThemeList: List<DefaultTheme>): RecyclerView.Adapter<DefaultThemeAdapter.DefaultThemeViewHolder>() {
    class DefaultThemeViewHolder(defaultThemeItemBinding: DefaultThemeItemBinding) :
        RecyclerView.ViewHolder(defaultThemeItemBinding.root) {
        val defaultThemeItemBinding: DefaultThemeItemBinding = defaultThemeItemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultThemeViewHolder {
       val defaultThemeItemBinding= DefaultThemeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DefaultThemeViewHolder(defaultThemeItemBinding)
    }

    override fun getItemCount(): Int {
        return defaultThemeList.size
    }

    override fun onBindViewHolder(holder: DefaultThemeViewHolder, position: Int) {
        val defaultThemeItem=defaultThemeList[position]
        holder.defaultThemeItemBinding.bgDfTheme.setBackgroundResource(defaultThemeItem.defaultThemeBg)
        holder.defaultThemeItemBinding.round4DfTheme.setImageResource(defaultThemeItem.defaultThemeRound4)
        holder.defaultThemeItemBinding.round3DfTheme.setImageResource(defaultThemeItem.defaultThemeRound3)
        holder.defaultThemeItemBinding.round2DfTheme.setImageResource(defaultThemeItem.defaultThemeRound2)
        holder.defaultThemeItemBinding.roundCenterDfTheme.setImageResource(defaultThemeItem.defaultThemeRoundCenter)
        holder.defaultThemeItemBinding.bellDfTheme.setImageResource(defaultThemeItem.defaultThemeBell)
        holder.defaultThemeItemBinding.smallLeftDfTheme.setImageResource(defaultThemeItem.defaultThemeSmallLeft)
        holder.defaultThemeItemBinding.bigLeftDfTheme.setImageResource(defaultThemeItem.defaultThemeBigLeft)
        holder.defaultThemeItemBinding.smallRightDfTheme.setImageResource(defaultThemeItem.defaultThemeSmallRight)
        holder.defaultThemeItemBinding.bigRightDfTheme.setImageResource(defaultThemeItem.defaultThemeBigRight)
        holder.defaultThemeItemBinding.activeThemeButton.setImageResource(defaultThemeItem.defaultThemePremium)
    }
}