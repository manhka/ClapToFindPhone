package com.example.claptofindphone

import android.app.Application
import android.content.res.Configuration
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        SharePreferenceUtils.init(this)
        val curLanguage = SharePreferenceUtils.getLanguageCode()
        setAppLocale(curLanguage)
    }
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}