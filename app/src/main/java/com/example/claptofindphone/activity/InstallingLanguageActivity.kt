package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityInstallingLanguageBinding
import com.example.claptofindphone.model.Constant

class InstallingLanguageActivity : BaseActivity() {
    private lateinit var installingLanguageBinding: ActivityInstallingLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeBackPressCallBack {  }
        installingLanguageBinding = ActivityInstallingLanguageBinding.inflate(layoutInflater)
        val view = installingLanguageBinding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // list of language
        val languageList = listOf(
            Constant.Country.ENGLISH,
            Constant.Country.VIETNAM,
            Constant.Country.FRENCH,
            Constant.Country.INDIA,
            Constant.Country.INDONESIA,
            Constant.Country.JAPAN,
            Constant.Country.BRAZILIAN,
            Constant.Country.KOREAN,
            Constant.Country.TURKEY
        )
        // load language
        val handler = Handler(Looper.getMainLooper())
        var index = 0
        val runnable = object : Runnable {
            override fun run() {
                if (index < languageList.size) {
                    installingLanguageBinding.txtLanguageName.text = languageList[index]
                    handler.postDelayed(this,300)
                    index++
                }else{
                    lifecycleScope.launchWhenResumed {
                        val intent= Intent(this@InstallingLanguageActivity,IntroductionActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        }
        handler.post(runnable)
    }




}