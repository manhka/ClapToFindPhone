package com.example.claptofindphone.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.LanguageItemBinding
import com.example.claptofindphone.model.Language

class LanguageAdapter(
    private val languageList: List<Language>,
    private val onClickItem:()-> Unit
) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    // ViewHolder class binding the data to the layout
    class LanguageViewHolder(binding: LanguageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var languageItemBinding: LanguageItemBinding = binding
    }

    private var selectedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        // Inflating the layout and returning the ViewHolder
        val binding =
            LanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        // Binding data to the views in the ViewHolder
        val language = languageList[position]
        holder.languageItemBinding.languageBackground.setBackgroundResource(language.languageBg)
        holder.languageItemBinding.imgViewLanguageFlag.setImageResource(language.languageFlag)
        holder.languageItemBinding.imgViewLanguageName.text = language.languageName
        holder.languageItemBinding.imgViewLanguageSelected.setImageResource(language.selected)
        if (position == selectedPosition) {
            holder.languageItemBinding.languageBackground.setBackgroundResource(R.drawable.bg_btn_blue_border)
            holder.languageItemBinding.imgViewLanguageSelected.setImageResource(R.drawable.active_radio)
        } else {
            holder.languageItemBinding.languageBackground.setBackgroundResource(R.drawable.bg_btn_grey)
            holder.languageItemBinding.imgViewLanguageSelected.setImageResource(R.drawable.passive_radio)
        }
        holder.itemView.setOnClickListener {
            onClickItem()
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

        }
    }
    fun getSelectedLanguage(): Language? {
        return if (selectedPosition != -1) {
            languageList[selectedPosition]
        } else {
            null
        }
    }
    fun clickCurrentLanguage(){
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }
}
