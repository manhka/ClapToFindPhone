package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityBecomeVipMemberBinding

class BecomeVipMemberActivity : BaseActivity() {
    private lateinit var becomeVipMemberBinding: ActivityBecomeVipMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeBackPressCallBack {
            val intent= Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        becomeVipMemberBinding=ActivityBecomeVipMemberBinding.inflate(layoutInflater)
        setContentView(becomeVipMemberBinding.root)
        becomeVipMemberBinding.backButton.setOnClickListener {
            val intent= Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}