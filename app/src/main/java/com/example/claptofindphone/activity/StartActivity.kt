package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityStartBinding
import com.example.claptofindphone.utils.SharePreferenceUtils

class StartActivity : BaseActivity() {
    private lateinit var startActivityBinding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityBinding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(startActivityBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startActivityBinding.startButton.setOnClickListener {
            val intent= Intent(this,VipActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
    }
}