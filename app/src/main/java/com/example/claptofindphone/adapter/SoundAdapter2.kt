package com.example.claptofindphone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.SoundItemBinding
import com.example.claptofindphone.model.Sound

class SoundAdapter2(
    val context: Context,
    val soundList: List<Sound>,
    val selectedSoundId:Int,
    val onItemSelected: (Sound) -> Unit):RecyclerView.Adapter<SoundAdapter2.SoundHolder>() {
    var isProcessingClick = false
    var selectedPosition = soundList.indexOfFirst { it.id == selectedSoundId }
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
            holder.soundItemBinding.txtSoundName.setTextAppearance(holder.itemView.context,R.style.TextSoundActive)


        } else {
            holder.soundItemBinding.customSoundBtn.setBackgroundResource(R.drawable.bg_sound_passive)
            holder.soundItemBinding.txtSoundName.setTextAppearance(holder.itemView.context,R.style.TextSoundDeActive)



        }

        holder.itemView.setOnClickListener {
            if (isProcessingClick) return@setOnClickListener

            isProcessingClick = true

            selectedPosition = position
            notifyDataSetChanged()
            onItemSelected(sound)
            // Reset sau 500ms hoặc khi xử lý xong
            it.postDelayed({
                isProcessingClick = false
            }, 500)
        }
    }
}