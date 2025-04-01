    package com.example.claptofindphone.activity

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.claptofindphone.R
    import com.example.claptofindphone.adapter.LanguageAdapter
    import com.example.claptofindphone.databinding.ActivityLanguageBinding
    import com.example.claptofindphone.model.Constant
    import com.example.claptofindphone.model.Language
    import com.example.claptofindphone.utils.InstallData
    import com.example.claptofindphone.utils.SharePreferenceUtils

    class LanguageActivity : BaseActivity() {
        private lateinit var binding: ActivityLanguageBinding
        private lateinit var languageList: List<Language>
        private lateinit var languageAdapter: LanguageAdapter
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLanguageBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val isNavigateFromHome = intent.getBooleanExtra("navigate_from_home", false)
            if(isNavigateFromHome){
                changeBackPressCallBack {
                    finish()
                }
            }else{
                changeBackPressCallBack {  }
            }

            // list of language
            languageList = InstallData.getLanguageList()
            // language sharePres
            var current_language_code:String = SharePreferenceUtils.getLanguageCode()
            if (current_language_code !in getLanguageAppList()){
                current_language_code="en"
            }
            val newLanguageList = ArrayList<Language>()
            for (language in languageList) {
                if (language.languageCode != current_language_code) {
                    newLanguageList.add(language)
                }
            }
            languageAdapter = LanguageAdapter(newLanguageList) {

                binding.layoutCurrentLanguage.setBackgroundResource(R.drawable.bg_btn_grey)
                binding.selectedCurrentLanguage.setImageResource(R.drawable.passive_radio)
            }
            // load language
            loadSavedLanguage()
            // set up rcv
            with(binding) {
                rcvLanguage.layoutManager = LinearLayoutManager(this@LanguageActivity)
                rcvLanguage.adapter = languageAdapter
            }

            // apply btn
            binding.btnApplyLanguage.setOnClickListener {
                val selectedLanguage = languageAdapter.getSelectedLanguage()

                if (selectedLanguage != null) {
                    SharePreferenceUtils.setLanguageCode(selectedLanguage.languageCode)
                }
                if (isNavigateFromHome) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    val intent = Intent(this, InstallingLanguageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                finish()
            }

            if (isNavigateFromHome) {
                binding.imgViewBack.visibility = View.VISIBLE
                binding.imgViewBack.setOnClickListener {
                    finish()
                }
            } else {
                binding.imgViewBack.visibility = View.GONE
            }
            binding.layoutCurrentLanguage.setOnClickListener {
                binding.layoutCurrentLanguage.setBackgroundResource(R.drawable.bg_btn_blue_border)
                binding.selectedCurrentLanguage.setImageResource(R.drawable.active_radio)
                languageAdapter.clickCurrentLanguage()
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
                "su" -> Constant.Country.INDONESIA
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
                "su" -> R.drawable.indonesia
                "ja" -> R.drawable.japan
                "pt" -> R.drawable.brazil
                "ko" -> R.drawable.south_korea
                "tr" -> R.drawable.turkey
                else -> R.drawable.english
            }

            binding.flagCurrentLanguage.setImageResource(flagResource)
            binding.nameCurrentLanguage.text = languageName
        }
        private fun getLanguageAppList(): List<String> {
            val languageAppList = listOf(
                "en",
                "vi",
                "fr",
                "hi",
                "su",
                "ja",
                "pt",
                "ko",
                "tr"
            )
            return languageAppList
        }

    }