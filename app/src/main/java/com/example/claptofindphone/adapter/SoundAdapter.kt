package com.example.claptofindphone.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.ChangeSoundActivity
import com.example.claptofindphone.databinding.SoundItemBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Sound
// sound adapter in home
class SoundAdapter(
    private val context: Context,
    private val soundList: List<Sound>) :
    RecyclerView.Adapter<SoundAdapter.SoundHolder>() {
    val soundSharedPreferences =
        context.getSharedPreferences(Constant.SharePres.SOUND_SHARE_PRES, MODE_PRIVATE)
    val selectedSound = soundSharedPreferences.getString(
        Constant.SharePres.ACTIVE_SOUND_NAME,
        context.getString(Constant.Sound.CAT)
    )
    var selectedPosition = soundList.indexOfFirst { it.soundName == selectedSound }
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
        Log.d("seleeccdcdcd",selectedSound.toString())
        val sound = soundList[position]
        holder.soundItemBinding.customSoundBtn.setImageResource(sound.soundIcon)
        holder.soundItemBinding.customSoundBtn.setBackgroundResource(sound.soundBg)
        holder.soundItemBinding.txtSoundName.text=sound.soundName
        if (position == selectedPosition) {
            holder.soundItemBinding.customSoundBtn.setBackgroundResource(R.drawable.bg_sound_active)
        } else {
            holder.soundItemBinding.customSoundBtn.setBackgroundResource(R.drawable.bg_sound_passive)
        }
        holder.itemView.setOnClickListener{
            val intent= Intent(context,ChangeSoundActivity::class.java)
            intent.putExtra("sound_type",sound.soundType)
            intent.putExtra("sound_name",sound.soundName)
            context.startActivity(intent)
        }
    }
}