package com.example.claptofindphone.adapter

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.EditThemeActivity
import com.example.claptofindphone.databinding.DefaultThemeItemBinding
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.utils.SharePreferenceUtils

class DefaultThemeAdapter(
    val context: Context,
    val defaultThemeList: List<DefaultTheme>

) : RecyclerView.Adapter<DefaultThemeAdapter.DefaultThemeViewHolder>() {
    private var mLastClickTime:Long = 0

    class DefaultThemeViewHolder(defaultThemeItemBinding: DefaultThemeItemBinding) :
        RecyclerView.ViewHolder(defaultThemeItemBinding.root) {
        val defaultThemeItemBinding: DefaultThemeItemBinding = defaultThemeItemBinding
    }

    val defaultThemeName = SharePreferenceUtils.getThemeName()
    val selectedPosition = defaultThemeList.indexOfFirst { it.themeName == defaultThemeName }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultThemeViewHolder {
        val defaultThemeItemBinding =
            DefaultThemeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultThemeViewHolder(defaultThemeItemBinding)
    }

    override fun getItemCount(): Int {
        return defaultThemeList.size
    }
    override fun onBindViewHolder(holder: DefaultThemeViewHolder, position: Int) {
        val defaultThemeItem = defaultThemeList[position]
        holder.defaultThemeItemBinding.bgDfTheme.setBackgroundResource(defaultThemeItem.defaultThemeBg)
        holder.defaultThemeItemBinding.bellDfTheme.setImageResource(defaultThemeItem.defaultTheme)
        holder.defaultThemeItemBinding.roundCircleDfTheme.setImageResource(defaultThemeItem.defaultThemeRoundCircle)
        holder.defaultThemeItemBinding.activeThemeButton.setImageResource(defaultThemeItem.defaultThemeSelected)
        if (position == selectedPosition) {
            holder.defaultThemeItemBinding.activeThemeButton.setImageResource(R.drawable.active_theme_ic)
        } else {
            holder.defaultThemeItemBinding.activeThemeButton.setImageResource(0)
        }
        holder.itemView.setOnClickListener {
            if (SystemClock.elapsedRealtime()-mLastClickTime<1000){
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(context, EditThemeActivity::class.java)
            intent.putExtra("default_theme", defaultThemeItem)
            context.startActivity(intent)
        }
    }
}