package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.IntroViewPagerAdapter
import com.example.claptofindphone.databinding.ActivityIntroductionBinding

class IntroductionActivity : BaseActivity() {

    private lateinit var introductionBinding: ActivityIntroductionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introductionBinding=ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(introductionBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.introduction_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val adapter = IntroViewPagerAdapter(this)
        introductionBinding.viewPager2Intro.adapter=adapter
        introductionBinding.nextButton.setOnClickListener{
            val currentItem = introductionBinding.viewPager2Intro.currentItem
            if (currentItem < adapter.itemCount - 1) {
                introductionBinding.viewPager2Intro.currentItem = currentItem + 1
            } else {
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
            }
        }

        introductionBinding.viewPager2Intro.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onChangeDot()
                if (position == adapter.itemCount - 1) {
                    introductionBinding.nextButton.setBackgroundResource(R.drawable.bg_btn_blue_border)
                    introductionBinding.nextButton.text = getString(R.string.start_now)
                    onChangeDot()
                } else {
                    introductionBinding.nextButton.setBackgroundResource(0)
                    introductionBinding.nextButton.text = getString(R.string.next)
                }
            }
        })

    }

    override fun onBackPressed() {
    }

    private fun onChangeDot() {
        when (introductionBinding.viewPager2Intro.currentItem) {
            0 -> {
                introductionBinding.dot1Button.setImageResource(R.drawable.dot_active)
                introductionBinding.dot2Button.setImageResource(R.drawable.dot)
                introductionBinding.dot3Button.setImageResource(R.drawable.dot)
                introductionBinding.dot4Button.setImageResource(R.drawable.dot)
            }

            1 -> {
                introductionBinding.dot1Button.setImageResource(R.drawable.dot)
                introductionBinding.dot2Button.setImageResource(R.drawable.dot_active)
                introductionBinding.dot3Button.setImageResource(R.drawable.dot)
                introductionBinding.dot4Button.setImageResource(R.drawable.dot)
            }

            2 -> {
                introductionBinding.dot1Button.setImageResource(R.drawable.dot)
                introductionBinding.dot2Button.setImageResource(R.drawable.dot)
                introductionBinding.dot3Button.setImageResource(R.drawable.dot_active)
                introductionBinding.dot4Button.setImageResource(R.drawable.dot)
            }

            3 -> {
                introductionBinding.dot1Button.setImageResource(R.drawable.dot)
                introductionBinding.dot2Button.setImageResource(R.drawable.dot)
                introductionBinding.dot3Button.setImageResource(R.drawable.dot)
                introductionBinding.dot4Button.setImageResource(R.drawable.dot_active)
            }
        }
    }

}