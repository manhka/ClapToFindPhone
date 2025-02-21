package com.example.claptofindphone.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.LanguageAdapter
import com.example.claptofindphone.databinding.ActivityLanguageBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Language
import com.example.claptofindphone.service.MyService
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class LanguageActivity : BaseActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var languageList: List<Language>
    private lateinit var languageAdapter: LanguageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.language_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // prevent back press
        val myService = MyService()
        myService.handleBackPress(this)
        // list of language
        languageList =InstallData.getLanguageList()
        // language sharePres
        val current_language_code =SharePreferenceUtils.getLanguageCode()
        val newLanguageList = ArrayList<Language>()
        for (language in languageList) {
            if (language.languageCode != current_language_code) {
                newLanguageList.add(language)
            }
        }
        languageAdapter = LanguageAdapter(newLanguageList)
        // load language
        loadSavedLanguage()
        // set up rcv
        with(binding) {
            rcvLanguage.layoutManager = LinearLayoutManager(this@LanguageActivity)
            rcvLanguage.adapter = languageAdapter
        }
        val isNavigateFromHome=intent.getBooleanExtra("navigate_from_home",false)
        // apply btn
        binding.btnApplyLanguage.setOnClickListener {
            val selectedLanguage = languageAdapter.getSelectedLanguage()

            if (selectedLanguage != null) {
                SharePreferenceUtils.setLanguageCode(selectedLanguage.languageCode)
            }
            if (isNavigateFromHome){
                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, IntroductionActivity::class.java)
                startActivity(intent)
                finish()
            }
            finish()
        }

        if (isNavigateFromHome){
            binding.imgViewBack.visibility= View.VISIBLE
            binding.imgViewBack.setOnClickListener {
                finish()
            }

        }else{
            binding.imgViewBack.visibility= View.GONE
        }


    }


    private fun loadSavedLanguage() {
        val savedLanguageCode = SharePreferenceUtils.getLanguageCode()
        updateCurrentLanguageDisplay(savedLanguageCode)
    }

    private fun updateCurrentLanguageDisplay(languageCode: String) {
        val languageName = when (languageCode) {
            "vi" -> Constant.Country.VIETNAM
            "fr" -> Constant.Country.FRENCH
            "hi" -> Constant.Country.INDIA
            "id" -> Constant.Country.INDONESIA
            "ja" -> Constant.Country.JAPAN
            "pt" -> Constant.Country.BRAZILIAN
            "ko" -> Constant.Country.KOREAN
            "tr" -> Constant.Country.TURKEY
            else -> Constant.Country.ENGLISH
        }

        val flagResource = when (languageCode) {
            "vi" -> R.drawable.vietnam
            "fr" -> R.drawable.france
            "hi" -> R.drawable.india
            "id" -> R.drawable.indonesia
            "ja" -> R.drawable.japan
            "pt" -> R.drawable.brazil
            "ko" -> R.drawable.south_korea
            "tr" -> R.drawable.turkey
            else -> R.drawable.english
        }

        binding.flagCurrentLanguage.setImageResource(flagResource)
        binding.nameCurrentLanguage.text = languageName
    }

    override fun onBackPressed() {
    }
}