package com.example.claptofindphone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.claptofindphone.fragment.how_to_use.HowToUseChargerAlarmFragment
import com.example.claptofindphone.fragment.how_to_use.HowToUseClapFragment
import com.example.claptofindphone.fragment.how_to_use.HowToUseDontTouchPhoneFragment
import com.example.claptofindphone.fragment.how_to_use.HowToUsePocketModeFragment
import com.example.claptofindphone.fragment.how_to_use.HowToUseVoicePasscodeFragment

class HowToUseAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(
        HowToUseClapFragment(),
        HowToUseVoicePasscodeFragment(),
        HowToUseDontTouchPhoneFragment(),
        HowToUsePocketModeFragment(),
        HowToUseChargerAlarmFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}