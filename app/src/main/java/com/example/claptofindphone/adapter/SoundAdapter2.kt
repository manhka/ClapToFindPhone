package com.example.claptofindphone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.SoundItemBinding
import com.example.claptofindphone.model.Sound

class SoundAdapter2(
    val context: Context,
    val soundList: List<Sound>,
    val selectedSoundName:String,
    val onItemSelected: (Sound) -> Unit):RecyclerView.Adapter<SoundAdapter2.SoundHolder>() {

    var selectedPosition = soundList.indexOfFirst { it.soundName == selectedSoundName }
    class SoundHolder(soundItemBinding: SoundItemBinding) :
        RecyclerView.ViewHolder(soundItemBinding.root) {
        val soundItemBinding: SoundItemBinding = soundItemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
        val soundItemBinding =
            SoundItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundHolder(soundItemBinding)
    }

    override fun getItemCount(): Int {
        return soundList.size
    }

    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        val sound = soundList[position]
        holder.soundItemBinding.customSoundBtn.setImageResource(sound.soundIcon)
        holder.soundItemBinding.customSoundBtn.setBackgroundResource(sound.soundBg)
        holder.soundItemBinding.txtSoundName.text=sound.soundName

        if (position == selectedPosition) {
            holder.soundItemBinding.customSoundBtn.setBackgroundResource(R.drawable.bg_sound_active)
        } else {
            holder.soundItemBinding.customSoundBtn.setBackgroundResource(R.drawable.bg_sound_passive)

        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onItemSelected(sound)
        }
    }
}