package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import com.example.claptofindphone.R
import com.example.claptofindphone.utils.SharePreferenceUtils

class LoadScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeBackPressCallBack {  }
        setContentView(R.layout.activity_load_screen)

        val isFromFoundPhone = SharePreferenceUtils.isFoundPhone()
        if (isFromFoundPhone) {
            val intent = Intent(this, FoundPhoneActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
    }

}