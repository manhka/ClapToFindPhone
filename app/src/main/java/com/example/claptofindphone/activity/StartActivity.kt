package com.example.claptofindphone.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        changeBackPressCallBack {  }
        startActivityBinding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(startActivityBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startActivityBinding.startButton.setOnClickListener {

            val timeComeToHome= SharePreferenceUtils.getTimeComeHome()
            if (timeComeToHome==0){
                val intent= Intent(this,ChangeSoundActivity::class.java)
                startActivity(intent)
                finish()
            }else
            if(timeComeToHome in 1..2){
                val intent= Intent(this,VipActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent= Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }


}