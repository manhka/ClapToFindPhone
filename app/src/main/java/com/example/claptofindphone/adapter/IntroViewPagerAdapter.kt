package com.example.claptofindphone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.claptofindphone.fragment.introduction.ClapAndWhistleFragment
import com.example.claptofindphone.fragment.introduction.PocketModeFragment
import com.example.claptofindphone.fragment.introduction.TouchPhoneFragment
import com.example.claptofindphone.fragment.introduction.VoicePasscodeFragment

class IntroViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> ClapAndWhistleFragment()
            1-> VoicePasscodeFragment()
            2-> TouchPhoneFragment()
            3-> PocketModeFragment()
            else -> ClapAndWhistleFragment()
        }
    }
}