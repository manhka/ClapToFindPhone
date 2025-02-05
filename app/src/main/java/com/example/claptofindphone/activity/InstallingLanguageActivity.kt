package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityInstallingLanguageBinding
import com.example.claptofindphone.model.Constant

class InstallingLanguageActivity : AppCompatActivity() {
    private lateinit var installingLanguageBinding: ActivityInstallingLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            "English",
            "Vietnamese",
            "French",
            "Hindi",
            "Indonesian",
            "Japanese",
            "Portuguese",
            "Korean",
            "Turkish"
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
                    navigate()
                }
            }
        }
        handler.post(runnable)
    }

    private fun navigate() {
        val firstTimeJoinSharePres= getSharedPreferences(Constant.SharePres.FIRST_TIME_JOIN_SHARE_PRES,
            MODE_PRIVATE)
        val isFirstTimeGetInApp= firstTimeJoinSharePres.getBoolean(Constant.SharePres.FIRST_TIME_GET_IN_APP,true)

        if(isFirstTimeGetInApp){
            firstTimeJoinSharePres.edit().putBoolean(Constant.SharePres.FIRST_TIME_GET_IN_APP,false).apply()
            val intent= Intent(this,LanguageActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            val intent= Intent(this,IntroductionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}