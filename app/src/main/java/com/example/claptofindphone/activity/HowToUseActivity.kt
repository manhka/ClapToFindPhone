package com.example.claptofindphone.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.HowToUseAdapter
import com.example.claptofindphone.databinding.ActivityHowToUseBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HowToUseActivity : AppCompatActivity() {
    private lateinit var howToUseBinding: ActivityHowToUseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        howToUseBinding = ActivityHowToUseBinding.inflate(layoutInflater)
        setContentView(howToUseBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.how_to_use_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tabLayout = howToUseBinding.tabHowToUse
        val viewPager = howToUseBinding.viewPager2HowToUse
        val viewPagerAdapter = HowToUseAdapter(this)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.clap_to_find)
                1 -> getString(R.string.voicePasscode)
                2 -> getString(R.string.dontTouchMyPhone)
                3 -> getString(R.string.pocketMode)
                4 -> getString(R.string.chargerAlarm)
                else -> ""
            }
        }.attach()

        for (i in 0 until tabLayout.tabCount) {
            val tabView = tabLayout.getTabAt(i)?.view
            val layoutParams = tabView?.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(20, 0, 20, 0)
            tabView.layoutParams = layoutParams
        }


    }
}
