package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityFoundPhoneBinding

class FoundPhoneActivity : AppCompatActivity() {
    private lateinit var foundPhoneBinding: ActivityFoundPhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foundPhoneBinding=ActivityFoundPhoneBinding.inflate(layoutInflater)
        setContentView(foundPhoneBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_found_phone)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        foundPhoneBinding.iFoundItButton.setOnClickListener {
            val intent= Intent(this,SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}