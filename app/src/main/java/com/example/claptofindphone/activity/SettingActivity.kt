package com.example.claptofindphone.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivitySettingBinding
import com.example.claptofindphone.databinding.DialogRateUsBinding
import com.example.claptofindphone.model.Constant

class SettingActivity : BaseActivity() {
    private lateinit var settingActivityBinding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingActivityBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(settingActivityBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        settingActivityBinding.languageButton.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("navigate_from_home", true)
            startActivity(intent)
        }
        settingActivityBinding.backButton.setOnClickListener {
            finish()
        }
        settingActivityBinding.shareButton.setOnClickListener {
            shareApp()
        }
        settingActivityBinding.policyButton.setOnClickListener {
            policy()
        }
        settingActivityBinding.feedbackButton.setOnClickListener {
            val deviceName = Build.MODEL // returns model name
            val deviceManufacturer = Build.MANUFACTURER

            val testIntent = Intent(Intent.ACTION_VIEW)
            val data: Uri = Uri.parse(
                """mailto:?subject=Clap To Find Phone &body=Device: $deviceManufacturer - $deviceName 
                        | ${getAndroidVersion()} 
                         &to=phuongculinh2015@gmail.com""".trimMargin()
            )
            testIntent.data = data
            try {
                startActivity(testIntent)

            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        settingActivityBinding.rateUsButton.setOnClickListener {
            val dialogBinding = DialogRateUsBinding.inflate(layoutInflater)
            // Create an AlertDialog with the inflated ViewBinding root
            val customDialog = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .create()

            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()

            dialogBinding.yesButton.setOnClickListener {
                customDialog.dismiss()
            }
            dialogBinding.noButton.setOnClickListener {
                customDialog.dismiss()
            }
            dialogBinding.exitButton.setOnClickListener {
                customDialog.dismiss()
            }

        }


    }
    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, Constant.URL_APP)
            startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun policy() {
        try {
            val uri = Uri.parse(Constant.URL_PRIVACY)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion (version $release)"
    }
}