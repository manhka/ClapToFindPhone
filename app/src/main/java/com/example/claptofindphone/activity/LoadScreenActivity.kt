package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.utils.SharePreferenceUtils

class LoadScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        changeBackPressCallBack {  }
        setContentView(R.layout.activity_load_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val isFromFoundPhone = SharePreferenceUtils.isFoundPhone()
        if (isFromFoundPhone) {
            val intent = Intent(this, FoundPhoneActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
    }

}