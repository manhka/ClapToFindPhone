package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityVipBinding

class VipActivity : BaseActivity() {
    private lateinit var vipBinding: ActivityVipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeBackPressCallBack {
            val intent= Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
       vipBinding=ActivityVipBinding.inflate(layoutInflater)
        setContentView(vipBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vip_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        vipBinding.backButton.setOnClickListener {
            val intent= Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        vipBinding.tryNowButton.setOnClickListener {
            val intent= Intent(this,BecomeVipMemberActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}