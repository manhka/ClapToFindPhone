package com.example.claptofindphone.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
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
import java.util.Locale

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var languageList: List<Language>
    private lateinit var languageAdapter: LanguageAdapter
    private lateinit var languageSharePres: SharedPreferences
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
        languageList = mutableListOf(
            Language(
                Constant.Country.ENGLISH,
                "en",
                R.drawable.bg_btn_grey,
                R.drawable.english,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.VIETNAM,
                "vi",
                R.drawable.bg_btn_grey,
                R.drawable.vietnam,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.FRENCH,
                "fr",
                R.drawable.bg_btn_grey,
                R.drawable.france,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.INDIA,
                "hi",
                R.drawable.bg_btn_grey,
                R.drawable.india,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.INDONESIA,
                "id",
                R.drawable.bg_btn_grey,
                R.drawable.indonesia,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.JAPAN,
                "ja",
                R.drawable.bg_btn_grey,
                R.drawable.japan,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.BRAZILIAN,
                "pt",
                R.drawable.bg_btn_grey,
                R.drawable.brazil,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.KOREAN,
                "ko",
                R.drawable.bg_btn_grey,
                R.drawable.south_korea,
                R.drawable.passive_radio
            ),
            Language(
                Constant.Country.TURKEY,
                "tr",
                R.drawable.bg_btn_grey,
                R.drawable.turkey,
                R.drawable.passive_radio
            )
        )
        // language sharePres
        languageSharePres =
            getSharedPreferences(Constant.SharePres.LANGUAGE_SHARE_PRES, MODE_PRIVATE)
        val current_language_code =
            languageSharePres.getString(Constant.SharePres.CURRENT_LANGUAGE, "en")
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
                setAppLocale(selectedLanguage.languageCode)
            } else {
                val curLanguage =
                    languageSharePres.getString(Constant.SharePres.CURRENT_LANGUAGE, "en") ?: "en"
                setAppLocale(curLanguage)
            }
            if (isNavigateFromHome){
                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
            }else{
                val intent = Intent(this, IntroductionActivity::class.java)
                startActivity(intent)
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

    // set up language for app
    private fun setAppLocale(languageCode: String) {
        // change current language
        languageSharePres.edit().putString(Constant.SharePres.CURRENT_LANGUAGE, languageCode)
            .apply()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }

    private fun loadSavedLanguage() {
        val savedLanguageCode =
            languageSharePres.getString(Constant.SharePres.CURRENT_LANGUAGE, "en") ?: "en"
        setAppLocale(savedLanguageCode)
        updateCurrentLanguageDisplay(savedLanguageCode)
    }

    private fun updateCurrentLanguageDisplay(languageCode: String) {
        val languageName = when (languageCode) {
            "vi" -> "Vietnamese"
            "fr" -> "French"
            "hi" -> "Hindi"
            "id" -> "Indonesian"
            "ja" -> "Japanese"
            "pt" -> "Portuguese (Brazil)"
            "ko" -> "Korean"
            "tr" -> "Turkish"
            else -> "English"
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