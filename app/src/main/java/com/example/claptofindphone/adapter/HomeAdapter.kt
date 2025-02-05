package com.example.claptofindphone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.claptofindphone.fragment.home.ChargerAlarmFragment
import com.example.claptofindphone.fragment.home.ClapToFindFragment
import com.example.claptofindphone.fragment.home.DontTouchMyPhoneFragment
import com.example.claptofindphone.fragment.home.PocketModeFragment
import com.example.claptofindphone.fragment.home.VoicePasscodeFragment

class HomeAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(
        ClapToFindFragment(),
        VoicePasscodeFragment(),
        PocketModeFragment(),
        ChargerAlarmFragment(),
        DontTouchMyPhoneFragment()
    )

    override fun getItemCount(): Int {
      return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
       return fragments[position]
    }
}