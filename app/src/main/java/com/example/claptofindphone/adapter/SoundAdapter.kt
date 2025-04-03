package com.example.claptofindphone.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.ChangeSoundActivity
import com.example.claptofindphone.databinding.SoundItemBinding
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.utils.SharePreferenceUtils

// sound adapter in home
class SoundAdapter(
    private val context: Context,
    private val soundList: List<Sound>) :
    RecyclerView.Adapter<SoundAdapter.SoundHolder>() {
    var isProcessingClick = false
    val selectedSoundId = SharePreferenceUtils.getSoundId()
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
        holder.itemView.setOnClickListener{
            if (isProcessingClick) return@setOnClickListener

            isProcessingClick = true

            val intent= Intent(context,ChangeSoundActivity::class.java)
            intent.putExtra("sound_type",sound.soundType)
            intent.putExtra("sound_id",sound.id)
            context.startActivity(intent)

            // Reset sau 500ms hoặc khi xử lý xong
            it.postDelayed({
                isProcessingClick = false
            }, 500)

        }
    }

}